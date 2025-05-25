package com.example.localloop;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.localloop.resources.Admin;
import com.example.localloop.resources.Organizer;
import com.example.localloop.resources.Participant;
import com.example.localloop.resources.UserAccount;

public class MainActivity extends AppCompatActivity {

    public static UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ValidateCredentials (View view) {
        String username;
        String password;
        String accountType;
        // retrieve username and password from input forms
        EditText userText = findViewById(R.id.username_input);
        username = (userText).getText().toString();
        EditText passText = findViewById(R.id.password_input);
        password = (passText).getText().toString();

        // TODO db query to determine if user exists
        // e.g. for all users in database compare if a username = username
        // TODO comparison between password and password
        // e.g. compare user's password with the password inputted
        // TODO create UserAccount instance with database provided accountType
        // get accountType from database
        accountType = "participant";

        if (accountType == "admin") {
            user = new Admin(username, password);
        } else if (accountType == "organizer") {
            user = new Organizer(username, password);
        } else if (accountType == "participant") {
            user = new Participant(username, password);
        } else {
            Log.e("Error","Nonexistent or invalid account type");
        }

        if (true || (username.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0"))) { // if user exists and password is correct
            HandleValidCredentials(view);
        } else {
            HandleInvalidCredentials(view);
        }
    }

    public void HandleValidCredentials (View view) {
        Intent intent;
        if (user instanceof Admin) {
            intent = new Intent(this, WelcomeAdmin.class);
        } else if (user instanceof Organizer) {
            intent = new Intent(this, WelcomeOrganizer.class);
        } else {
            intent = new Intent(this, WelcomeParticipant.class);
        }
        startActivity(intent);
    }

    public void HandleInvalidCredentials (View view) {
        // clear username and password fields
        final TextView clearUser = (TextView) findViewById(R.id.username_input);
        clearUser.setText("");
        final TextView clearPass = (TextView) findViewById(R.id.password_input);
        clearPass.setText("");

        // state bad credentials
        final TextView badCreds = (TextView) findViewById(R.id.badcreds_state);
        badCreds.setText("Invalid credentials, please try again.");
    }
    public void onCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }
}