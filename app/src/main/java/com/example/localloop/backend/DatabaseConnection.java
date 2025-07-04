package com.example.localloop.backend;

import android.util.Log;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    public static  ArrayList<Participant> allParticipants = new ArrayList<>();
    private static boolean participantsLoaded = false;

    public DatabaseConnection() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        getAllParticipants(new ParticipantCallback() {
            @Override
            public void onParticipantsLoaded(ArrayList<Participant> participants) {
                allParticipants = participants;
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            Log.e("Unable to connect to database","");
        }
        Log.d("Participants",String.valueOf(DatabaseConnection.allParticipants));
    }

    public interface ParticipantCallback {
        void onParticipantsLoaded(ArrayList<Participant> participants);
    }

    public static void getAllParticipants(ParticipantCallback callback) {
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
                    Log.d("Participant stored locally:",p.toString());
                    allParticipants.add(p);
                }
                participantsLoaded = true;
                callback.onParticipantsLoaded(new ArrayList<>(allParticipants));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseConnection", "Error loading participants: " + error.getMessage());
                callback.onParticipantsLoaded(new ArrayList<>());
            }
        });
    }
}

