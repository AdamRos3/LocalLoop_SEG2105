package com.example.localloop;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ValidateCredentials (View view) {
        // set username and password
        //EditText userText = (EditText) findViewById(R.id.username_input);
        EditText userText = findViewById(R.id.username_input);
        username = (userText).getText().toString();
        //EditText passText = (EditText) findViewById(R.id.password_input);
        EditText passText = findViewById(R.id.password_input);
        password = (passText).getText().toString();
        //final TextView toChange = (TextView) findViewById(R.id.app_name);
        //toChange.setText(username);

        // temporary hardcoded credentials for testing
        if (username.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0")) {
            // Launch WelcomeActivity with username and role
            Intent intent = new Intent(this, WelcomeAdmin.class);
            intent.putExtra("username", username);
            intent.putExtra("role", "Admin");
            startActivity(intent);
        } else {
            //DB Query
            HandleInvalidCredentials(view);
        }
    }

    public void onCreateAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
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

}