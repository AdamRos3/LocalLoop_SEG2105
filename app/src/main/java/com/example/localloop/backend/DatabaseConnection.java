package com.example.localloop.backend;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseConnection {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef = database.getReference();
    private static ArrayList<Participant> allParticipants = new ArrayList<Participant>();
    private static ArrayList<Organizer> allOrganizers = new ArrayList<Organizer>();
    public DatabaseConnection(String username, String password) {
        getAllParticipants();
        getAllOrganizers();
    }
    public static ArrayList<Participant> getAllParticipants() {
        myRef.child("Participant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String password = userSnapshot.child("password").getValue(String.class);
                    String userID = userSnapshot.child("userID").getValue(String.class);
                    allParticipants.add(new Participant(username, password, userID));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
            }
        });
        return allParticipants;
    }
    public static ArrayList<Organizer> getAllOrganizers() {
            myRef.child("Organizer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    String password = userSnapshot.child("password").getValue(String.class);
                    String userID = userSnapshot.child("userID").getValue(String.class);
                    allOrganizers.add(new Organizer(username, password, userID));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
            }
        });
        return allOrganizers;
    }
}
