package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.model.Admin;
import com.example.localloop.model.DatabaseConnection;

public class WelcomeAdmin extends AppCompatActivity {

    protected static Admin admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_admin);

        // Set window insets to maintain future layout compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.returnToLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        admin = (Admin)DatabaseInstance.get().getUser();
        
        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message2);
        String message = "Welcome " + admin.getUsername();
        welcomeMessage.setText(message);
    }

    public void ReturnToLogin(View view) {
        //Intent intent = new Intent(this, MainActivity.class);
        finish();
        //startActivity(intent);
    }

    public void toManageUsers(View view) {
        Intent intent = new Intent(this, ManageUsers.class);
        startActivity(intent);
    }
    public void toManageEvents(View view) {
        Intent intent = new Intent(this, ManageEventCategories.class);
        startActivity(intent);
    }

}