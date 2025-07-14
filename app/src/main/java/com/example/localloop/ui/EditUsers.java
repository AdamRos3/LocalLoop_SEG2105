package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.model.Admin;
import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.Organizer;
import com.example.localloop.model.Participant;
import com.example.localloop.model.UserAccount;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.example.localloop.resources.exception.NoSuchUserException;

public class EditUsers extends AppCompatActivity {
    private Admin admin;
    private UserAccount userToEdit;


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

        admin = (Admin) DatabaseInstance.get().getUser();
        userToEdit = ManageUsers.userToEdit;

        TextView userID_text = findViewById(R.id.userID_text);
        EditText username_text = findViewById(R.id.editUsername_field);
        EditText password_text = findViewById(R.id.editPassword_field);

        userID_text.setText(userToEdit.getUserID());
        username_text.setText(userToEdit.getUsername());
        password_text.setText(userToEdit.getPassword());

        Spinner spinner = findViewById(R.id.editAccountType_field);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.options_array,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
        );
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (userToEdit instanceof Organizer) {
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
        EditText username_field = findViewById(R.id.editUsername_field);
        EditText password_field = findViewById(R.id.editPassword_field);
        Spinner accountType_field = findViewById(R.id.editAccountType_field);

        String username = username_field.getText().toString();
        String password = password_field.getText().toString();
        new Thread(() -> {
            try {
                if (accountType_field.getSelectedItem().equals("Organizer")) {
                    DatabaseInstance.get().createNewUser(new Organizer(username, password, null));
                } else {
                    DatabaseInstance.get().createNewUser(new Participant(username, password, null));
                }
            } catch (InvalidEventNameException e) {
                Log.e("InvalidUsername", "Username Taken");
            } catch (InterruptedException e) {
                Log.e("InterruptedException", "Interrupted at onCreateUserAccount > CreateAccount");
            }
        }).start();
        onDeleteAccount(view);
    }

    public void onDeleteAccount(View view) {
        new Thread(() -> {
            try {
                admin.deleteUser(DatabaseInstance.get(), userToEdit);
            } catch (NoSuchUserException e) {
                Log.e("NoSuchUserException", "Nonexisting user cannot be deleted");
            } catch (NoSuchEventException e) {
                Log.e("NoSuchEventException","User's events cannot be deleted");
            }
            catch (InterruptedException e) {
                Log.e("InterruptedException", "Interrupted at onCreateUserAccount > onDeleteAccount");
            }
        }).start();
        finish();
    }
}