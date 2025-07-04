package com.example.localloop.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseConnector {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    public static void createNewParticipant(String username, String password) {
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(username, password, key));
    }
    public static void createNewOrganizer(String username, String password) {
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(username, password, key));
    }
}
