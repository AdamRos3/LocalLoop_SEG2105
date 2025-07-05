package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Organizer;
import com.example.localloop.backend.Participant;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ManageUsers extends AppCompatActivity {

    private ArrayList<Organizer> allOrganizers = new ArrayList<Organizer>();
    private ArrayList<Participant> allParticipants = new ArrayList<Participant>();
    private DatabaseConnection dbConnection;
    private Admin admin;
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
        listView = findViewById(R.id.users_List);
        dbConnection = DatabaseInstance.get();
        admin = (Admin)dbConnection.getUser();
        new Thread(() -> {
            try {
                allParticipants = admin.getAllParticipants(dbConnection);
                allOrganizers = admin.getAllOrganizers(dbConnection);
                runOnUiThread(() -> {
                    arrayAdapter = new ArrayAdapter<>(ManageUsers.this, android.R.layout.simple_list_item_1);
                    for (Organizer o : allOrganizers) {
                        arrayAdapter.add(o.getUsername());
                    }
                    listView.setAdapter(arrayAdapter);
                });
            } catch (InterruptedException e) {
                Log.e("InterruptedException","Call from ManageUsers onCreate");
                finish();
            }
        }).start();
        Intent intent = new Intent(this, EditUsers.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = (String)adapterView.getItemAtPosition(i);
                Log.d(username, "Username from getItemAtPosition(i) = "+username);
                String userID = "";
                String password = "";
                // To add userID
                if (accountTypeSelected.equals("Organizer")) {
                    for (Organizer o : allOrganizers) {
                        if (o.getUsername().equals(username)) {
                            userID = o.getUserID();
                            password = o.getPassword();
                            break;
                        }
                    }
                } else {
                    for (Participant p : allParticipants) {
                        if (p.getUsername().equals(username)) {
                            userID = p.getUserID();
                            password = p.getPassword();
                            break;
                        }
                    }
                }
                intent.putExtra("accountType", accountTypeSelected);
                intent.putExtra("userID", userID);
                intent.putExtra("username",username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                arrayAdapter.clear();
                if (tab.getPosition() == 0) {
                    for(Organizer o: allOrganizers) {
                        arrayAdapter.add(o.getUsername());
                        accountTypeSelected = "Organizer";
                    }
                    listView = findViewById(R.id.users_List);
                    listView.setAdapter(arrayAdapter);
                } else if (tab.getPosition() == 1) {
                    for(Participant p: allParticipants) {
                        arrayAdapter.add(p.getUsername());
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
}