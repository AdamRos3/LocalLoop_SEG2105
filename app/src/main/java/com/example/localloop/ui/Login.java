package com.example.localloop.ui;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.localloop.R;
import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Organizer;
import com.example.localloop.backend.UserAccount;
import com.example.localloop.resources.exception.exception.NoSuchUserException;

public class Login extends AppCompatActivity {


    protected static DatabaseConnection dbConnection;
    private static UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ValidateCredentials(View view) {
        String username;
        String password;

        EditText userText = findViewById(R.id.username_input);
        username = (userText).getText().toString();
        EditText passText = findViewById(R.id.password_input);
        password = (passText).getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            HandleInvalidCredentials(view);
            return;
        }

        new Thread(() -> {
            try {
                dbConnection = new DatabaseConnection(username, password);
                user = dbConnection.getUser();
                runOnUiThread(() -> HandleValidCredentials(view));
            } catch (NoSuchUserException e) {
                runOnUiThread(() -> HandleInvalidCredentials(view));
            } catch (InterruptedException e) {
                Log.e("InterruptedException", "At database login", e);
                runOnUiThread(() -> HandleInvalidCredentials(view));
            }
        }).start();
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
        // clear username and password fields
        final TextView clearUser = (TextView) findViewById(R.id.username_input);
        clearUser.setText("");
        final TextView clearPass = (TextView) findViewById(R.id.password_input);
        clearPass.setText("");
        // Go to next actvity
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