package com.example.localloop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.resources.Organizer;
import com.example.localloop.resources.Participant;
import com.example.localloop.resources.UserAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditUsers extends AppCompatActivity {

    private String accountType;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_users);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        accountType = getIntent().getStringExtra("accountType");
        userID = getIntent().getStringExtra("userID");

        TextView userID_text = findViewById(R.id.userID_text);
        EditText username_text = findViewById(R.id.editTextText);
        EditText password_text = findViewById(R.id.editTextText2);

        userID_text.setText(userID);
        username_text.setText(getIntent().getStringExtra("username"));
        password_text.setText(getIntent().getStringExtra("password"));

        Spinner spinner = findViewById(R.id.accountType_Spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.options_array,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
        );
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (accountType.equals("Organizer")) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(this, ManageUsers.class);
        startActivity(intent);
    }

    public void onEditSubmit(View view) {

    }

    public void onDeleteAccount(View view) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/Participant");;
        if (accountType.equals("Organizer")) {
            myRef = FirebaseDatabase.getInstance().getReference("users/Organizer");
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Log.d("DataSnapshot Value", childSnapshot.getValue().toString());
                    Log.d("DataSnapshot Child Value", childSnapshot.child("userID").getValue().toString());
                    if (childSnapshot.child("userID").getValue().equals(userID)) {
                        childSnapshot.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Intent intent = new Intent(this, ManageUsers.class);
        startActivity(intent);
    }
}