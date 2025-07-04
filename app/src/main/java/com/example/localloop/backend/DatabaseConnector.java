package com.example.localloop.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseConnector {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    public static void createNew(Participant p) {
        String key = myRef.push().getKey();
        myRef.child("users/Participant").child(key).setValue(new Participant(p.getUsername(), p.getPassword(), key));
    }
    public static void createNew(Organizer o) {
        String key = myRef.push().getKey();
        myRef.child("users/Organizer").child(key).setValue(new Organizer(o.getUsername(), o.getPassword(), key));
    }
}
