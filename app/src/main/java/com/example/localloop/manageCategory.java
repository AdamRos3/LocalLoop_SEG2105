package com.example.localloop;

import android.content.Context;
import android.provider.ContactsContract;
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
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class manageCategory extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);
        RecyclerView recyclerview = findViewById(R.id.category_recycler_view);
        List<Category> categoryList = new ArrayList<>();

        //Testing data
        //categoryList.add(new Category("Sports","Outdoor and recreational events"));
        //categoryList.add(new Category("Workshops","Education"));

        categoryAdapter adapter = new categoryAdapter(this, categoryList);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("categories");

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {

                        category.id = categorySnapshot.getKey(); //firebase key
                        categoryList.add(category);

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(manageCategory.this, "Failed to load categories", Toast.LENGTH_SHORT).show();

            }
        });

        //UI
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

            String id = categoryRef.push().getKey(); //generates unique ID
            Category newCategory = new Category(id, name, description);
            categoryRef.child(id).setValue(newCategory);

            Toast.makeText(this,"Category Added!", Toast.LENGTH_SHORT).show();
            nameInput.setText("");
            descriptionInput.setText("");




        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v1 -> finish());
    }

    public static class Category {
        public String id;

        public String name;
        public String description;


        public Category() { //firebase needs this
            //this.id = id;
            //this.name = name;
            //this.description = description;
        }

        public Category(String id, String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
    public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.CategoryViewHolder> {
        private List<Category> categoryList;
        private Context context;

        public categoryAdapter(Context context, List<Category> categoryList) {
            this.context = context;
            this.categoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categoryList.get(position);
            holder.nameTextView.setText(category.name);
            holder.descTextView.setText(category.description);

            holder.editButton.setOnClickListener(v -> {
                //Toast.makeText(context, "Edit: " + category.name, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Category");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputName = new EditText(context);
                inputName.setText(category.name);
                layout.addView(inputName);

                final EditText inputDescription = new EditText(context);
                inputDescription.setText(category.description);
                layout.addView(inputDescription);

                builder.setView(layout);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newDescription = inputDescription.getText().toString().trim();

                    if (!newName.isEmpty() && !newDescription.isEmpty()) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(category.id);
                        ref.setValue(new Category(category.id, newName, newDescription));
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
                        .setMessage("Are you sure you want to delete \"" + category.name +"\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories").child(category.id);
                            ref.removeValue();
                            Toast.makeText(context,"Category Deleted", Toast.LENGTH_SHORT).show();

                        })
                        .setNegativeButton("Cancel",null)
                        .show();

            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
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
