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

import com.example.localloop.resources.UserAccount;

public class WelcomeAdmin extends AppCompatActivity {


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

        UserAccount user = MainActivity.user;
        String username = user.getUsername();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.ManageEventCategories2);
        String message = "Welcome " + username + "! You are logged in as admin";
        welcomeMessage.setText(message);
    }

    public void ReturnToLogin(View view) {
        //Intent intent = new Intent(this, MainActivity.class);
        finish();
        //startActivity(intent);
    }

    //public void toManageUsers(View view) {
        //Intent intent = new Intent(this, ManageUsers.class);
        //startActivity(intent);

    public void toManageEvents(View view) {

        Intent intent = new Intent(this, manageCategory.class);
        startActivity(intent);
    }
}