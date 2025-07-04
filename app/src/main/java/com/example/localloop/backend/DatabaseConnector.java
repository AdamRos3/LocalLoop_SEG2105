package com.example.localloop.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseConnector {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    public static void createNew(Participant p) {
        String key = myRef.push().getKey();
        p.setUserID(key);
        myRef.child("users/Participant").child(key).setValue(p);
    }
    public static void createNew(Organizer o) {
        String key = myRef.push().getKey();
        o.setUserID(key);
        myRef.child("users/Organizer").child(key).setValue(o);
    }
}
