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
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Organizer;

public class WelcomeOrganizer extends AppCompatActivity {
    private static DatabaseConnection dbConnection;
    private static Organizer user;
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
        user = (Organizer)dbConnection.getUser();
        String username = user.getUsername();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        String message = "Welcome " + user.toString();
        welcomeMessage.setText(message);
    }
    public void onManageEvents(View view) {
        Intent intent = new Intent(this, ManageEvents.class);
        startActivity(intent);
    }
    public void returnToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}