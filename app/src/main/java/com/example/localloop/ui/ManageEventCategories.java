package com.example.localloop.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.localloop.model.Admin;
import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.EventCategory;
import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageEventCategories extends AppCompatActivity {
    private final ArrayList<EventCategory> allCategories = new ArrayList<>();
    private Admin admin;
    private categoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        admin = (Admin) DatabaseInstance.get().getUser();

        RecyclerView recyclerview = findViewById(R.id.event_recycler_view);
        adapter = new categoryAdapter(this, allCategories);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);

        fetchAndRefreshCategories();

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

            new Thread(() -> {
                try {
                    admin.createEventCategory(DatabaseInstance.get(), newCategory);
                    runOnUiThread(() -> {
                        nameInput.setText("");
                        descriptionInput.setText("");
                        Toast.makeText(this, "Category Added!", Toast.LENGTH_SHORT).show();
                        fetchAndRefreshCategories();
                    });
                } catch (InvalidEventCategoryNameException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Category name is taken", Toast.LENGTH_SHORT).show();
                        nameInput.setText("");
                    });
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchAndRefreshCategories() {
        new Thread(() -> {
            try {
                List<EventCategory> latest = admin.getAllEventCategories(DatabaseInstance.get());
                runOnUiThread(() -> {
                    allCategories.clear();
                    allCategories.addAll(latest);
                    adapter.notifyDataSetChanged();
                });
            } catch (InterruptedException e) {
                Log.e("ManageEventCategories", "Failed to fetch categories", e);
            }
        }).start();
    }

    public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {
        private final List<EventCategory> allCategories;
        private final Context context;

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
                        new Thread(() -> {
                            try {
                                admin.editEventCategory(DatabaseInstance.get(), category, newName, newDescription);
                                fetchAndRefreshCategories();
                            } catch (InvalidEventNameException e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(context, "Event category name taken", Toast.LENGTH_SHORT).show();
                                });
                            } catch (NoSuchEventCategoryException e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(context, "Event category does not exist", Toast.LENGTH_SHORT).show();
                                });
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                        Toast.makeText(context, "Category Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete \"" + category.getName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    admin.deleteEventCategory(DatabaseInstance.get(),category);
                                } catch (NoSuchEventCategoryException e) {
                                    runOnUiThread(() -> Toast.makeText(context, "Category does not exist!", Toast.LENGTH_SHORT).show());
                                } catch (NoSuchEventException e) {
                                    runOnUiThread(() -> Toast.makeText(context, "Category deletion event error", Toast.LENGTH_SHORT).show());
                                } catch (InterruptedException e) {
                                    runOnUiThread(() -> Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show());
                                }
                            }).start();
                            runOnUiThread(() -> Toast.makeText(context, "Category Deleted", Toast.LENGTH_SHORT).show());
                            fetchAndRefreshCategories();
                        })
                        .setNegativeButton("Cancel", null)
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

