package com.example.localloop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.resources.Admin;
import com.example.localloop.resources.Organizer;
import com.example.localloop.resources.Participant;
import com.example.localloop.resources.UserAccount;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageUsers extends AppCompatActivity {

    private ArrayList<Organizer> organizerList = new ArrayList<Organizer>();
    private ArrayList<Participant> participantList = new ArrayList<Participant>();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private TabLayout tabLayout;
    private String accountTypeSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateUserLists();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        for(int i =0; i<organizerList.size(); i++) {
            arrayAdapter.add((organizerList.get(i)).getUsername());
        }
        listView = findViewById(R.id.users_List);
        listView.setAdapter(arrayAdapter);
        Intent intent = new Intent(this, EditUsers.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = (String)adapterView.getItemAtPosition(i);
                String userID = "";
                // To add userID
                if (accountTypeSelected == "Organizer") {
                    for (int j =0; j< organizerList.size(); j++) {
                        if (organizerList.get(j).getUsername() == username) {
                            userID = organizerList.get(j).getUserID();
                            break;
                        }
                    }
                } else {
                    for (int j =0; j< organizerList.size(); j++) {
                        if (participantList.get(j).getUsername() == username) {
                            userID = participantList.get(j).getUserID();
                            break;
                        }
                    }
                }
                intent.putExtra(userID, "userID");
                intent.putExtra(username, "username");
                startActivity(intent);
            }
        });

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                arrayAdapter.clear();
                if (tab.getPosition() == 0) {
                    for(int i =0; i<organizerList.size(); i++) {
                        arrayAdapter.add((organizerList.get(i)).getUsername());
                        accountTypeSelected = "Organizer";
                    }
                    listView = findViewById(R.id.users_List);
                    listView.setAdapter(arrayAdapter);
                } else if (tab.getPosition() == 1) {
                    for(int i =0; i<organizerList.size(); i++) {
                        arrayAdapter.add((participantList.get(i)).getUsername());
                        accountTypeSelected = "Participant";
                    }
                    listView = findViewById(R.id.users_List);
                    listView.setAdapter(arrayAdapter);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    private void updateUserLists() {
        // Clear Lists
        organizerList.removeAll(organizerList);
        participantList.removeAll(participantList);
        // Get Organizers
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/Organizer");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        organizerList.add(childSnapshot.getValue(Organizer.class));
                    }
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // Get Participants
        myRef = FirebaseDatabase.getInstance().getReference("users/Participant");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    participantList.add(childSnapshot.getValue(Participant.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}