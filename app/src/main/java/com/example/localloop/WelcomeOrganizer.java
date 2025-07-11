package com.example.localloop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.resources.UserAccount;

public class WelcomeOrganizer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_organizer);

        // Set window insets to maintain future layout compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.returnToLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserAccount user = MainActivity.user;
        String username = user.getUsername();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.ManageEventCategories2);
        String message = "Welcome " + username + "! You are logged in as organizer.";
        welcomeMessage.setText(message);
    }

    public void ReturnToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}