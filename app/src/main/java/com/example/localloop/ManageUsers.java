package com.example.localloop;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class ManageUsers extends AppCompatActivity {

    private ArrayList<String> organizerList = new ArrayList<String>(
            Arrays.asList("Organizer"));
    private ArrayList<String> participantList = new ArrayList<String>(
            Arrays.asList("Participant"));

    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private TabLayout tabLayout;

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
        tabLayout = findViewById(R.id.tabLayout);

        // Initialize adapter with organizerList
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(organizerList));
        listView.setAdapter(arrayAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(organizerList);
                } else if (tab.getPosition() == 1) {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(participantList);
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
        for(int i = 0; i<15; i++) {
            organizerList.add("Organizer "+i);
            participantList.add("Participant "+i);
        }
    }

}