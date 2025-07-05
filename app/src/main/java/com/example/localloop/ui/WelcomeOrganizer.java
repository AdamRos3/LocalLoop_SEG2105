package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Organizer;
import com.example.localloop.backend.UserAccount;

public class WelcomeOrganizer extends AppCompatActivity {
    private static DatabaseConnection dbConnection;
    private static Organizer organizer;
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
        dbConnection = DatabaseInstance.get();
        organizer = (Organizer)dbConnection.getUser();
        String username = organizer.getUsername();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        String message = "Welcome " + organizer.toString();
        welcomeMessage.setText(message);
    }

    public void ReturnToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}