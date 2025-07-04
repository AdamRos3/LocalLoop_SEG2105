package com.example.localloop.backend;

import android.util.Log;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {
    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private static UserAccount user;
    public static ArrayList<Participant> allParticipants = new ArrayList<>();
    public static ArrayList<Organizer> allOrganizers = new ArrayList<>();
    private static boolean participantsLoaded = false;
    private static boolean organizersLoaded = false;

    // Public methods
    public DatabaseConnection(String username, String password) throws DatabaseConnectionException {
        try {
            updateAllUsers();

            // Now you can safely check the lists here (synchronously)
            Log.d("Participants", String.valueOf(allParticipants));
            Log.d("Organizers", String.valueOf(allOrganizers));

            // Authenticate
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
                throw new DatabaseConnectionException("Invalid Login Credentials");
            } else {
                Log.d("Valid login credentials", "");
            }

        } catch (InterruptedException e) {
            Log.e("DatabaseConnection", "Interrupted while waiting for data", e);
            throw new DatabaseConnectionException("Database Connection Interrupted");
        }
    }

    public UserAccount getUser() {
        return user;
    }

    // Protected methods
    protected static boolean createNew(Participant p) {
        // need to update users before searching
        // this.updateAllUsers();
        String key = myRef.push().getKey(); // Firebase generated unique ID key
        // Search all participants to check for username conflicts
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
        return true;
    }
    protected static boolean createNew(Organizer o) {
        // need to update users before searching
        // updateAllUsers();
        String key = myRef.push().getKey(); // Firebase generated unique ID key
        // Search all organizers to check for username conflicts
        myRef.child("users/Organizer").child(key).setValue(new Participant(o.getUsername(), o.getPassword(), key));
        return true;
    }

    // Private methods
    private void updateAllUsers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2); // One for each list

        getAllParticipants(new DatabaseCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                allParticipants = participants;
                participantsLoaded = true;
                latch.countDown();
            }

            @Override
            public void onOrganizersLoaded(ArrayList<Organizer> organizers) {
                // not needed here
            }
        });

        getAllOrganizers(new DatabaseCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                // not needed here
            }

            @Override
            public void onOrganizersLoaded(ArrayList<Organizer> organizers) {
                allOrganizers = organizers;
                organizersLoaded = true;
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            Log.e("DatabaseConnection", "Timeout: Could not load all users in time");
        }
    }

    private static void getAllParticipants(DatabaseCallback callback) {
        if (participantsLoaded) {
            callback.onParticipantsLoaded(new ArrayList<>(allParticipants));
            return;
        }

        myRef.child("users/Participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allParticipants.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Participant p = userSnapshot.getValue(Participant.class);
                    if (p != null) {
                        Log.d("Participant stored locally:", p.toString());
                        allParticipants.add(p);
                    }
                }
                callback.onParticipantsLoaded(new ArrayList<>(allParticipants));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading participants: " + error.getMessage());
                callback.onParticipantsLoaded(new ArrayList<>());
            }
        });
    }

    private static void getAllOrganizers(DatabaseCallback callback) {
        if (organizersLoaded) {
            callback.onOrganizersLoaded(new ArrayList<>(allOrganizers));
            return;
        }

        myRef.child("users/Organizer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allOrganizers.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Organizer o = userSnapshot.getValue(Organizer.class);
                    if (o != null) {
                        Log.d("Organizer stored locally:", o.toString());
                        allOrganizers.add(o);
                    }
                }
                callback.onOrganizersLoaded(new ArrayList<>(allOrganizers));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading organizers: " + error.getMessage());
                callback.onOrganizersLoaded(new ArrayList<>());
            }
        });
    }

    private interface DatabaseCallback {
        void onParticipantsLoaded(ArrayList<Participant> participants);
        void onOrganizersLoaded(ArrayList<Organizer> organizers);
    }

}