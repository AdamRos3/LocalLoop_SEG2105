package com.example.localloop.backend;

import android.util.Log;

import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryNameException;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private static UserAccount user;

    private static ArrayList<Participant> allParticipants = new ArrayList<>();
    private static ArrayList<Organizer> allOrganizers = new ArrayList<>();
    private static ArrayList<Admin> allAdmins = new ArrayList<>();
    private static ArrayList<EventCategory> allEventCategories = new ArrayList<>();

    private static boolean participantsLoaded = false;
    private static boolean organizersLoaded = false;
    private static boolean adminsLoaded = false;
    private static boolean eventCategoriesLoaded = false;

    // Public methods
    public DatabaseConnection(String username, String password) throws NoSuchEventCategoryNameException, InterruptedException {
        updateAllUsers();
        updateAllEventCategories();

        Log.d("Participants", String.valueOf(allParticipants));
        Log.d("Organizers", String.valueOf(allOrganizers));
        Log.d("Admins", String.valueOf(allAdmins));
        Log.d("EventCategories", String.valueOf(allEventCategories));

        boolean found = false;

        for (Participant p : allParticipants) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                user = p;
                found = true;
                break;
            }
        }
        if (!found) {
            for (Organizer o : allOrganizers) {
                if (o.getUsername().equals(username) && o.getPassword().equals(password)) {
                    user = o;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            for (Admin a : allAdmins) {
                if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                    user = a;
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            throw new NoSuchEventCategoryNameException("User does not exist");
        }
    }

    public UserAccount getUser() {
        return user;
    }

    public static void createNewUser(Participant p) throws InvalidEventCategoryNameException, InterruptedException {
        updateAllUsers();
        for (Participant existing : allParticipants) {
            if (p.getUsername().equals(existing.getUsername())) {
                Log.d(p.getUsername(), "New Participant Username Conflict");
                throw new InvalidEventCategoryNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
    }

    public static void createNewUser(Organizer o) throws InvalidEventCategoryNameException, InterruptedException {
        updateAllUsers();
        for (Organizer existing : allOrganizers) {
            if (o.getUsername().equals(existing.getUsername())) {
                Log.e(o.getUsername(), "New Organizer Username Conflict");
                throw new InvalidEventCategoryNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(o.getUsername(), o.getPassword(), key));
    }

    // Protected methods
    protected static void createEventCategory(EventCategory category) throws InvalidEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();
        boolean found = false;
        // Check that Event Category does not exist
        for (EventCategory existing : allEventCategories) {
            if (category.getName().equals(existing.getName())) {
                Log.e(category.getName(), "New Organizer Username Conflict");
                throw new InvalidEventCategoryNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("categories").child(key).setValue(new EventCategory(category.getName(),category.getDescription(),key));
    }
    protected static void deleteEventCategory(EventCategory categoryToDelete) throws NoSuchEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();
        boolean found = false;
        // Check that Event Category exists
        for (EventCategory existing : allEventCategories) {
            if (categoryToDelete.getCategoryID().equals(existing.getCategoryID())) {
                found = true;
            }
        }
        if(!found) {
            Log.e(categoryToDelete.getName(), "EventCategory Not Found");
            throw new NoSuchEventCategoryNameException("EventCategory does not exist");
        }
        myRef.child("categories").child(categoryToDelete.getCategoryID()).removeValue();
    }
    protected static void editEventCategory(EventCategory categoryToEdit, String name, String description) throws NoSuchEventCategoryNameException, InvalidEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();
        boolean found = false;
        boolean duplicate = false;
        // Check that Event Category exists
        for (EventCategory existing : allEventCategories) {
            if (categoryToEdit.getCategoryID().equals(existing.getCategoryID())) {
                found = true;
            }
            if (name.equals(existing.getName())) {
                duplicate = true;
            }
        }
        if(!found) {
            Log.e(categoryToEdit.getName(), "EventCategory Not Found");
            throw new NoSuchEventCategoryNameException("EventCategory does not exist");
        }
        if(duplicate) {
            Log.e(categoryToEdit.getName(), "EventCategory name conflict");
            throw new InvalidEventCategoryNameException("EventCategory Name taken");
        }
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("name").setValue(name);
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("description").setValue(description);
    }
    protected static void deleteUser(UserAccount userToDelete) throws NoSuchEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllUsers();
        boolean found = false;

        if (user instanceof Organizer) {
            // Check that user exists
            for (Organizer o : allOrganizers) {
                if (o.getUserID().equals(userToDelete.getUserID())) {
                    found = true;
                }
            }
            if (!found) {
                Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
                throw new NoSuchEventCategoryNameException("Nonexisting user cannot be deleted");
            }
            myRef.child("users/Organizer").child(userToDelete.getUserID()).removeValue();
        } else {
            // Check that user exists
            for (Participant p : allParticipants) {
                if (p.getUserID().equals(userToDelete.getUserID())) {
                    found = true;
                }
            }
            if (!found) {
                Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
                throw new NoSuchEventCategoryNameException("Nonexisting user cannot be deleted");
            }
            myRef.child("users/Participant").child(userToDelete.getUserID()).removeValue();
        }
    }
    protected ArrayList<Participant> getAllParticipants() throws InterruptedException {
        // Called by Admin Class Only
        updateAllUsers(); // TODO create updateAllParticipants specific method
        return allParticipants;
    }
    protected ArrayList<Organizer> getAllOrganizers() throws InterruptedException {
        // Called by Admin Class Only
        updateAllUsers(); // TODO create updateAllOrganizers specific method
        return allOrganizers;
    }


    // Private Methods
    private interface DatabaseUserCallback {
        void onParticipantsLoaded(ArrayList<Participant> participants);
        void onOrganizersLoaded(ArrayList<Organizer> organizers);
        void onAdminsLoaded(ArrayList<Admin> admins);
    }
    private interface DatabaseEventCategoryCallback {
        void onEventCategoriesLoaded(ArrayList<EventCategory> categories);
    }
    private static void getAllUsers(DatabaseUserCallback callback) {
        myRef.child("users/Participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Participant> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Participant p = child.getValue(Participant.class);
                    if (p != null) temp.add(p);
                }
                callback.onParticipantsLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading participants", error.toException());
                callback.onParticipantsLoaded(new ArrayList<>());
            }
        });

        myRef.child("users/Organizer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Organizer> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Organizer o = child.getValue(Organizer.class);
                    if (o != null) temp.add(o);
                }
                callback.onOrganizersLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading organizers", error.toException());
                callback.onOrganizersLoaded(new ArrayList<>());
            }
        });

        myRef.child("users/Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Admin> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Admin a = child.getValue(Admin.class);
                    if (a != null) temp.add(a);
                }
                callback.onAdminsLoaded(temp);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading admins", error.toException());
                callback.onAdminsLoaded(new ArrayList<>());
            }
        });
    }
    private static void getAllEventCategories(DatabaseEventCategoryCallback callback) {
        myRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<EventCategory> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    EventCategory c = child.getValue(EventCategory.class);
                    if (c != null) temp.add(c);
                }
                callback.onEventCategoriesLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading event categories", error.toException());
                callback.onEventCategoriesLoaded(new ArrayList<>());
            }
        });
    }
    private static void updateAllUsers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        Log.d("updateAllUsers", "Starting...");

        getAllUsers(new DatabaseUserCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                Log.d("updateAllUsers", "Participants loaded");
                allParticipants = participants;
                participantsLoaded = true;
                latch.countDown();
            }

            @Override
            public void onOrganizersLoaded(ArrayList<Organizer> organizers) {
                allOrganizers = organizers;
                Log.d("updateAllUsers", "Organizers loaded");
                organizersLoaded = true;
                latch.countDown();
            }

            @Override
            public void onAdminsLoaded(ArrayList<Admin> admins) {
                Log.d("updateAllUsers", "Admins loaded");
                allAdmins = admins;
                adminsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }
    public static void updateAllEventCategories() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllEventCategories", "Starting...");

        getAllEventCategories(new DatabaseEventCategoryCallback() {
            @Override
            public void onEventCategoriesLoaded(ArrayList<EventCategory> category) {
                Log.d("updateAllEventCategories", "EventCategories loaded");
                allEventCategories = category;
                eventCategoriesLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }
}
