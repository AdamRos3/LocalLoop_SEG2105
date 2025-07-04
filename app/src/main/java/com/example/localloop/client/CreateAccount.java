package com.example.localloop.client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.backend.DatabaseConnector;
import com.example.localloop.backend.Organizer;
import com.example.localloop.backend.Participant;
import com.example.localloop.backend.UserAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends AppCompatActivity {

    public String username;
    public String password;
    public String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        Switch accountTypeSwitch = findViewById(R.id.accountTypeSwitch);
        TextView accountTypeText = findViewById(R.id.newAccountType_input);
        accountTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accountType = accountTypeSwitch.isChecked() ? "Participant" : "Organizer";
                accountTypeText.setText(accountType);
            }
        });
    }

    public void onCreateUserAccount(View view) {

        EditText userText = findViewById(R.id.username_input);
        username = (userText).getText().toString();
        //EditText passText = (EditText) findViewById(R.id.password_input);
        EditText passText = findViewById(R.id.password_input);
        password = (passText).getText().toString();
        Intent intent = new Intent(this, MainActivity.class);
        Switch accountSwitch = findViewById(R.id.accountTypeSwitch);
        if (accountSwitch.isChecked()) {
            DatabaseConnector.createNewParticipant(username, password);
        } else {
            DatabaseConnector.createNewOrganizer(username, password);
        }
        startActivity(intent);
    }
}