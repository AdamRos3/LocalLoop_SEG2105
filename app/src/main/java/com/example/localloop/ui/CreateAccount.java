package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.Organizer;
import com.example.localloop.model.Participant;
import com.example.localloop.resources.exception.InvalidEventNameException;

public class CreateAccount extends AppCompatActivity {

    private TextView badCreds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        badCreds = findViewById(R.id.badcreds_state);
        badCreds.setVisibility(View.GONE);

        Switch accountTypeSwitch = findViewById(R.id.accountTypeSwitch);
        TextView accountTypeText = findViewById(R.id.newAccountType_input);
        accountTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accountTypeText.setText(isChecked ? "Participant" : "Organizer");
            }
        });
    }

    public void onCreateUserAccount(View view) {

        EditText userText = findViewById(R.id.username_input);
        String username = (userText).getText().toString();

        EditText passText = findViewById(R.id.password_input);
        String password = (passText).getText().toString();

        Switch accountSwitch = findViewById(R.id.accountTypeSwitch);

        //validates if the username field or the password field are empty upon account creation
        if (username.isEmpty()) {
            userText.setError("Username required");
            userText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passText.setError("Password required");
            passText.requestFocus();
            return;
        }

        new Thread(() -> {
            try {
                if (accountSwitch.isChecked()) {
                    DatabaseInstance.get().createNewUser(new Participant(username, password, null));
                } else {
                    DatabaseInstance.get().createNewUser(new Organizer(username, password, null));
                }
                finish();
            } catch (InvalidEventNameException e) {
                Log.e("InvalidUsername","Username Taken");
                runOnUiThread(() -> {
                    badCreds.setText("That username is already in use");
                    badCreds.setVisibility(View.VISIBLE);
                    userText.requestFocus();
                });

            } catch (InterruptedException e) {
                Log.e("InterruptedException","Interrupted at onCreateUserAccount > CreateAccount");
            }
        }).start();
    }
    public void onBackClicked(View view) {
        Intent intent = new Intent(this, WelcomeAdmin.class);
        startActivity(intent);
        finish();
    }
}