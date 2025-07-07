package com.example.localloop.ui;

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
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Organizer;
import com.example.localloop.backend.Participant;
import com.example.localloop.resources.exception.InvalidEventNameException;

public class CreateAccount extends AppCompatActivity {

    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        Switch accountTypeSwitch = findViewById(R.id.accountTypeSwitch);
        TextView accountTypeText = findViewById(R.id.newAccountType_input);
        accountTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                accountTypeText.setText(isChecked ? "Participant" : "Organizer");
            }
        });
        dbConnection = DatabaseInstance.get();
    }

    public void onCreateUserAccount(View view) {

        EditText userText = findViewById(R.id.username_input);
        String username = (userText).getText().toString();

        EditText passText = findViewById(R.id.password_input);
        String password = (passText).getText().toString();

        Switch accountSwitch = findViewById(R.id.accountTypeSwitch);

        new Thread(() -> {
            try {
                if (accountSwitch.isChecked()) {
                    dbConnection.createNewUser(new Participant(username, password, null));
                } else {
                    dbConnection.createNewUser(new Organizer(username, password, null));
                }
                finish();
            } catch (InvalidEventNameException e) {
                Log.e("InvalidUsername","Username Taken");
            } catch (InterruptedException e) {
                Log.e("InterruptedException","Interrupted at onCreateUserAccount > CreateAccount");
            }
        }).start();
    }
}