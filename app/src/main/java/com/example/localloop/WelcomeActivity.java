package com.example.localloop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Set window insets to maintain future layout compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.returnToLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from login screen
        String username = getIntent().getStringExtra("username");
        String role = getIntent().getStringExtra("role");

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        String message = "Welcome " + username + "! You are logged in as \"" + role + "\".";
        welcomeMessage.setText(message);
    }

    public void ReturnToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}