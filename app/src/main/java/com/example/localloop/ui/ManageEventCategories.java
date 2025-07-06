package com.example.localloop.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.EventCategory;
import com.example.localloop.backend.Organizer;
import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEventCategories extends AppCompatActivity {
    private static ArrayList<EventCategory> allCategories = new ArrayList<>();
    private static DatabaseConnection dbConnection;
    private static Admin admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        RecyclerView recyclerview = findViewById(R.id.category_recycler_view);

        dbConnection = DatabaseInstance.get();
        admin = (Admin)dbConnection.getUser();

        new Thread(() -> {
            try {
                allCategories = admin.getAllEventCategories(dbConnection);
            } catch (InterruptedException e) {
                Log.e("InterruptedException","Call from ManageUsers onCreate");
                finish();
            }
        }).start();

        categoryAdapter adapter = new categoryAdapter(this, allCategories);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);


        Button addButton = findViewById(R.id.add_category_button);
        EditText nameInput = findViewById(R.id.category_name_input);
        EditText descriptionInput = findViewById(R.id.category_description_input);

        addButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Both fields are required.", Toast.LENGTH_SHORT).show();
            return;
            }

            EventCategory newCategory = new EventCategory(name, description, null);
            try {
                admin.createEventCategory(dbConnection, newCategory);
            } catch (InvalidEventCategoryNameException e) {
                Toast.makeText(this,"Category name is taken", Toast.LENGTH_SHORT).show();
                nameInput.setText("");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Toast.makeText(this,"Category Added!", Toast.LENGTH_SHORT).show();
            nameInput.setText("");
            descriptionInput.setText("");




        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v1 -> finish());
    }
    public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {
        private List<EventCategory> allCategories;
        private Context context;

        public categoryAdapter(Context context, List<EventCategory> allCategories) {
            this.context = context;
            this.allCategories = allCategories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            EventCategory category = allCategories.get(position);
            holder.nameTextView.setText(category.getName());
            holder.descTextView.setText(category.getDescription());

            holder.editButton.setOnClickListener(v -> {
                //Toast.makeText(context, "Edit: " + category.name, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Category");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputName = new EditText(context);
                inputName.setText(category.getName());
                layout.addView(inputName);

                final EditText inputDescription = new EditText(context);
                inputDescription.setText(category.getDescription());
                layout.addView(inputDescription);

                builder.setView(layout);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newDescription = inputDescription.getText().toString().trim();

                    if (!newName.isEmpty() && !newDescription.isEmpty()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(category.getCategoryID());
                        ref.setValue(new EventCategory(newName, newDescription, category.getCategoryID()));
                        Toast.makeText(context, "Category Updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("Cancel",null);
                builder.show();

            });

            holder.deleteButton.setOnClickListener(v -> {
                //Toast.makeText(context, "Delete: " + category.name, Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete \"" + category.getName() +"\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(category.getCategoryID());
                            ref.removeValue();
                            Toast.makeText(context,"Category Deleted", Toast.LENGTH_SHORT).show();

                        })
                        .setNegativeButton("Cancel",null)
                        .show();

            });
        }

        @Override
        public int getItemCount() {
            return allCategories.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, descTextView;
            Button editButton, deleteButton;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.category_name);
                descTextView = itemView.findViewById(R.id.category_description);
                editButton = itemView.findViewById(R.id.edit_button);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}
