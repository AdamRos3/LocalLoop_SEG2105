package com.example.localloop.backend;

import android.util.Log;

import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.example.localloop.resources.exception.NoSuchUserException;
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
    private static ArrayList<Event> allEvents = new ArrayList<>();

    private static boolean participantsLoaded = false;
    private static boolean organizersLoaded = false;
    private static boolean adminsLoaded = false;
    private static boolean eventCategoriesLoaded = false;
    private static boolean eventsLoaded = false;

    // Public methods
    public DatabaseConnection(String username, String password) throws NoSuchUserException, InterruptedException {
        updateAllUsers();
        updateAllEventCategories();
        updateAllEvents();

        Log.d("Participants", String.valueOf(allParticipants));
        Log.d("Organizers", String.valueOf(allOrganizers));
        Log.d("Admins", String.valueOf(allAdmins));
        Log.d("EventCategories", String.valueOf(allEventCategories));
        Log.d("Events", String.valueOf(allEvents));

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
            throw new NoSuchUserException("User does not exist");
        }
    }

    public UserAccount getUser() {
        return user;
    }

    public static void createNewUser(Participant p) throws InvalidEventNameException, InterruptedException {
        updateAllUsers();
        for (Participant existing : allParticipants) {
            if (p.getUsername().equals(existing.getUsername())) {
                Log.d(p.getUsername(), "New Participant Username Conflict");
                throw new InvalidEventNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
    }

    public static void createNewUser(Organizer o) throws InvalidEventNameException, InterruptedException {
        updateAllUsers();
        for (Organizer existing : allOrganizers) {
            if (o.getUsername().equals(existing.getUsername())) {
                Log.e(o.getUsername(), "New Organizer Username Conflict");
                throw new InvalidEventNameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(o.getUsername(), o.getPassword(), key));
    }

    // Protected methods
    protected static void createEventCategory(EventCategory category) throws InvalidEventCategoryNameException, InterruptedException {
        // Called by Admin Class Only
        updateAllEventCategories();

        // Check that Event Category does not exist
        for (EventCategory existing : allEventCategories) {
            if (category.getName().equals(existing.getName())) {
                Log.e(category.getName(), "New EventCategory Name Conflict");
                throw new InvalidEventCategoryNameException("EventCategory Name taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("categories").child(key).setValue(new EventCategory(category.getName(),category.getDescription(),key));
    }
    protected static void deleteEventCategory(EventCategory categoryToDelete) throws NoSuchEventCategoryException, InterruptedException {
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
            throw new NoSuchEventCategoryException("EventCategory does not exist");
        }
        myRef.child("categories").child(categoryToDelete.getCategoryID()).removeValue();
    }
    protected static void editEventCategory(EventCategory categoryToEdit, String name, String description) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
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
            throw new NoSuchEventCategoryException("EventCategory does not exist");
        }
        if(duplicate) {
            Log.e(categoryToEdit.getName(), "EventCategory name conflict");
            throw new InvalidEventNameException("EventCategory Name taken");
        }
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("name").setValue(name);
        myRef.child("categories").child(categoryToEdit.getCategoryID()).child("description").setValue(description);
    }
    protected static void createEvent(Event event) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
        // Called by Organizer Class Only
        updateAllEvents();

        // Check that EventCategory exists
        boolean categoryFound = false;
        for (EventCategory existing : allEventCategories) {
            if ((event.getCategoryID().equals(existing.getCategoryID()))) {
                categoryFound = true;
            }
        }
        if (!categoryFound) {
            throw new NoSuchEventCategoryException("Event category does not exist");
        }

        // Check that Event Category does not exist
        for (Event existing : allEvents) {
            if (event.getName().equals(existing.getName())) {
                Log.e(event.getName(), "New Event Name Conflict");
                throw new InvalidEventNameException("Event name taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("events").child(key).setValue(new Event(event.getName(),event.getDescription(),event.getCategoryID(),event.getFee(),event.getDate(),event.getTime(),user.getUserID(),key));
    }
    protected static void editEvent(Event event, String name, String description, EventCategory category, double fee, Date date, Time time) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
        
    }
    protected static void deleteEvent(Event eventToDelete) throws NoSuchEventException, InterruptedException {
        // Called by Organizer Class Only
        updateAllEvents();
        boolean found = false;
        // Check that Event Category exists
        for (Event existing : allEvents) {
            if ((eventToDelete.getEventID()).equals(existing.getEventID())) {
                found = true;
            }
        }
        if(!found) {
            Log.e(eventToDelete.getName(), "Event Not Found");
            throw new NoSuchEventException("Event does not exist");
        }
        myRef.child("events").child(eventToDelete.getEventID()).removeValue();
    }
    protected static void deleteUser(UserAccount userToDelete) throws NoSuchUserException, InterruptedException {
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
                throw new NoSuchUserException("Nonexisting user cannot be deleted");
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
                throw new NoSuchUserException("Nonexisting user cannot be deleted");
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
    private interface DatabaseEventCallback {
        void onEventsLoaded(ArrayList<Event> events);
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
    private static void getAllEvents(DatabaseEventCallback callback) {
        myRef.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Event> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Event e = child.getValue(Event.class);
                    if (e != null) temp.add(e);
                }
                callback.onEventsLoaded(temp);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading events", error.toException());
                callback.onEventsLoaded(new ArrayList<>());
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
            public void onEventCategoriesLoaded(ArrayList<EventCategory> categories) {
                Log.d("updateAllEventCategories", "EventCategories loaded");
                allEventCategories = categories;
                eventCategoriesLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }
    public static void updateAllEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Log.d("updateAllEvents", "Starting...");

        getAllEvents(new DatabaseEventCallback() {
            @Override
            public void onEventsLoaded(ArrayList<Event> events) {
                Log.d("updateAllEvents", "Events loaded");
                allEvents = events;
                eventsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }
}
