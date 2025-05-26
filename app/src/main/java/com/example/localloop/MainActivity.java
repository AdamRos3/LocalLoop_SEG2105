package com.example.localloop;

import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.localloop.resources.Admin;
import com.example.localloop.resources.Organizer;
import com.example.localloop.resources.Participant;
import com.example.localloop.resources.UserAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static UserAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.AccountTypeSelector);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.options_array,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
        );
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

        Spinner spinner = (Spinner) findViewById(R.id.AccountTypeSelector);
        accountType = spinner.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            HandleInvalidCredentials(view);
            return;
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/" + accountType);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    switch (accountType) {
                        case "Participant":
                            user = childSnapshot.getValue(Participant.class);
                            break;
                        case "Organizer":
                            user = childSnapshot.getValue(Organizer.class);
                            break;
                        case "Admin":
                            user = childSnapshot.getValue(Admin.class);
                            break;
                        default:
                            user = childSnapshot.getValue(UserAccount.class);
                            break;
                    }
                    if (user.getUsername().equals(username)) {
                        if (user.getPassword().equals(password)) {
                            HandleValidCredentials(view);
                            return;
                        } else {
                            HandleInvalidCredentials(view);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                HandleInvalidCredentials(view);
            }
        });

        /**
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
         **/

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