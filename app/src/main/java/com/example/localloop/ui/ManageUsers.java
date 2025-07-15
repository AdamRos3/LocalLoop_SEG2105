package com.example.localloop.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.model.Admin;
import com.example.localloop.model.Organizer;
import com.example.localloop.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class ManageUsers extends AppCompatActivity {

    private static Admin admin;
    private ArrayList<UserAccount> allUsers = new ArrayList<>();
    private ManageUsers.userAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        admin = (Admin) DatabaseInstance.get().getUser();

        new Thread(() -> {
            try {
                for (UserAccount user : admin.getAllOrganizers(DatabaseInstance.get())) {
                    allUsers.add(user);
                }
                for (UserAccount user : admin.getAllParticipants(DatabaseInstance.get())) {
                    allUsers.add(user);
                }
                runOnUiThread(() -> {
                    RecyclerView usersListView = findViewById(R.id.listViewUsers);
                    adapter = new ManageUsers.userAdapter(this, allUsers);
                    usersListView.setLayoutManager(new LinearLayoutManager(this));
                    usersListView.setAdapter(adapter);
                });
            } catch (InterruptedException e) {
                Log.e("ManageEvents", e.toString());
                Toast.makeText(ManageUsers.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public void onBackClick(View view) {
        finish();
    }

    public static class userViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText, accountTypeText, userIDText, passwordText;
        ImageButton editButton, deleteButton;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.username);
            accountTypeText = itemView.findViewById(R.id.AccountType);
            userIDText = itemView.findViewById(R.id.userID);
            passwordText = itemView.findViewById(R.id.password);

            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public static class userAdapter extends RecyclerView.Adapter<ManageUsers.userViewHolder> {

        Context context;
        List<UserAccount> users;

        public userAdapter(Context context, List<UserAccount> users) {
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public ManageUsers.userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ManageUsers.userViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ManageUsers.userViewHolder holder, int position) {
            UserAccount user = users.get(position);
            holder.usernameText.setText(user.getUsername());

            String accountTypeStr = "";
            if (user instanceof Organizer) {
                accountTypeStr = "Organizer";
            } else {
                accountTypeStr = "Participant";
            }
            holder.accountTypeText.setText(accountTypeStr);
            holder.userIDText.setText(user.getUserID());
            holder.passwordText.setText(user.getPassword());

            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete \"" + user.getUsername() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    admin.deleteUser(DatabaseInstance.get(),user.getUserID());
                                    ((ManageUsers)context).allUsers.clear();
                                    for (UserAccount u : admin.getAllOrganizers(DatabaseInstance.get())) {
                                        ((ManageUsers)context).allUsers.add(u);
                                    }
                                    for (UserAccount u : admin.getAllParticipants(DatabaseInstance.get())) {
                                        ((ManageUsers)context).allUsers.add(u);
                                    }
                                    ((ManageUsers)context).runOnUiThread(() -> {
                                        ((ManageUsers)context).adapter.notifyDataSetChanged();
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(context, "Error cannot delete user", Toast.LENGTH_SHORT).show();
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}