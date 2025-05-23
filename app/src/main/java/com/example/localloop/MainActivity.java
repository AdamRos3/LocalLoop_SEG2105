package com.example.localloop;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
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
        EditText userText = (EditText) findViewById(R.id.username_input);
        username = (userText).getText().toString();
        EditText passText = (EditText) findViewById(R.id.password_input);
        password = (passText).getText().toString();
        //final TextView toChange = (TextView) findViewById(R.id.app_name);
        //toChange.setText(username);

        // DB query
        HandleInvalidCredentials(view);
    }

    public void HandleInvalidCredentials (View view) {
        // clear username and password fields
        final TextView clearUser = (TextView) findViewById(R.id.username_input);
        clearUser.setText("");
        final TextView clearPass = (TextView) findViewById(R.id.password_input);
        clearPass.setText("");

        // state bad credentials
        final TextView badCreds = (TextView) findViewById(R.id.badcreds_state);
        badCreds.setText("Invalid credentials, please try again");
    }

}