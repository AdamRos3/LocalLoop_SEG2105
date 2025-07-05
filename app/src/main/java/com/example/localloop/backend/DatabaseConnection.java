package com.example.localloop.backend;

import android.util.Log;

import com.example.localloop.exception.database.InvalidUsernameException;
import com.example.localloop.exception.database.NoSuchUserException;
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

    private static boolean participantsLoaded = false;
    private static boolean organizersLoaded = false;
    private static boolean adminsLoaded = false;

    // Public methods
    public DatabaseConnection(String username, String password) throws NoSuchUserException, InterruptedException {
        updateAllUsers();

        Log.d("Participants", String.valueOf(allParticipants));
        Log.d("Organizers", String.valueOf(allOrganizers));
        Log.d("Admins", String.valueOf(allAdmins));

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

    public static void createNew(Participant p) throws InvalidUsernameException, InterruptedException {
        updateAllUsers();
        for (Participant existing : allParticipants) {
            if (p.getUsername().equals(existing.getUsername())) {
                Log.d(p.getUsername(), "New Participant Username Conflict");
                throw new InvalidUsernameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
    }

    public static void createNew(Organizer o) throws InvalidUsernameException, InterruptedException {
        updateAllUsers();
        for (Organizer existing : allOrganizers) {
            if (o.getUsername().equals(existing.getUsername())) {
                Log.d(o.getUsername(), "New Organizer Username Conflict");
                throw new InvalidUsernameException("Username taken");
            }
        }
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(o.getUsername(), o.getPassword(), key));
    }
    // Protected methods

    // Private methods
    private interface DatabaseCallback {
        void onParticipantsLoaded(ArrayList<Participant> participants);
        void onOrganizersLoaded(ArrayList<Organizer> organizers);
        void onAdminsLoaded(ArrayList<Admin> admins);
    }

    private static void updateAllUsers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        getAllUsers(new DatabaseCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                allParticipants = participants;
                participantsLoaded = true;
                latch.countDown();
            }

            @Override
            public void onOrganizersLoaded(ArrayList<Organizer> organizers) {
                allOrganizers = organizers;
                organizersLoaded = true;
                latch.countDown();
            }

            @Override
            public void onAdminsLoaded(ArrayList<Admin> admins) {
                allAdmins = admins;
                adminsLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException("Timeout while waiting for Firebase data.");
        }
    }

    private static void getAllUsers(DatabaseCallback callback) {
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
}
