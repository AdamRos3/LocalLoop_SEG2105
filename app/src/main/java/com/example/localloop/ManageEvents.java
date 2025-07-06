package com.example.localloop;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.resources.Category;
import com.example.localloop.resources.Event;
import com.example.localloop.resources.Organizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageEvents extends AppCompatActivity {

    public static Organizer organizer;
    List<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_events);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        retrieveOrganizer();

        RecyclerView eventsListView = findViewById(R.id.listViewEvents);
        List<Event> events = new ArrayList<>();
        categories = retrieveCategoriesFromDatabase();

        eventAdapter adapter = new eventAdapter(this, events);
        eventsListView.setLayoutManager(new LinearLayoutManager(this));
        eventsListView.setAdapter(adapter);

        //Get Events from Database
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        if (event.getOrganizerID().equals(organizer.getUserID())) {
                            events.add(event);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ManageEventListener", error.toException());
                Toast.makeText(ManageEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }

    public void onAddClick(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Add Event");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this);
        inputName.setHint("Name");
        layout.addView(inputName);

        final EditText inputDescription = new EditText(this);
        inputDescription.setHint("Description");
        layout.addView(inputDescription);

        final Spinner categorySpinner = new Spinner(this);
        categorySpinner.setPrompt("Category");
        layout.addView(categorySpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        final EditText inputFee = new EditText(this);
        inputFee.setHint("Fee");
        layout.addView(inputFee);

        final EditText inputDate = new EditText(this);
        inputDate.setHint("Date (YYYY-MM-DD)");
        layout.addView(inputDate);

        final EditText inputTime = new EditText(this);
        inputTime.setHint("Time HH:MM");
        layout.addView(inputTime);

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String feeStr = inputFee.getText().toString().trim();
            String dateStr = inputDate.getText().toString().trim();
            String timeStr = inputTime.getText().toString().trim();


            if (name.isEmpty() || description.isEmpty() || feeStr.isEmpty()
                    || dateStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);

                String[] t = timeStr.split(":");
                Time timeObj = new Time(
                        Integer.parseInt(t[0]),
                        Integer.parseInt(t[1]));


                Event newEvent = new Event(name, description, dateTime, category, Double.parseDouble(fee), eventID, organizer.getUserID());
                eventsRef.child(eventID).setValue(newEvent);
                String[] d = dateStr.split("-");
                Date dateObj = new Date(
                        Integer.parseInt(d[0]),
                        Integer.parseInt(d[1]),
                        Integer.parseInt(d[2]));

                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
                String eventID = eventRef.push().getKey();

                Event newEvent = new Event(
                        name, description, dateObj,timeObj, category, fee, eventID, organizer.getUserID());

                eventRef.child(eventID).setValue(newEvent);
                Toast.makeText(this, "Event added",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this,"Invalid fee or date/time", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    private void retrieveOrganizer() {
        String organizerID = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            organizerID = extras.getString("organizerID");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference organizerRef = ref.child("Organizer").child(organizerID);

        organizerRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Organizer temp = task.getResult().getValue(Organizer.class);

                    if (temp != null) {
                        organizer = temp;
                        Log.d("firebase", "Data retrieved, Name: " + organizer.getUsername());
                    } else {
                        Log.e("firebase", "Organizer not found");
                    }

                }
            }
        });
    }

    private List<String> retrieveCategoriesFromDatabase() {
        //Get Categories from Database
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        List<String> categories = new ArrayList<>();

        categoriesRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    for (DataSnapshot categorySnapshot : task.getResult().getChildren()) {
                        Category category = categorySnapshot.getValue(Category.class);
                        if (category != null) {
                            categories.add(category.getName());
                        }
                    }
                }
            }
        });

        return categories;
    }

    public static class eventViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, categoryText, descriptionText, dateTimeText, feeText;
        ImageButton editButton, deleteButton;

        public eventViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name);
            categoryText = itemView.findViewById(R.id.category);
            descriptionText = itemView.findViewById(R.id.description);
            dateTimeText = itemView.findViewById(R.id.dateTime);
            feeText = itemView.findViewById(R.id.fee);

            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public static class eventAdapter extends RecyclerView.Adapter<eventViewHolder> {

        Context context;
        List<Event> events;

        public eventAdapter(Context context, List<Event> events) {
            this.context = context;
            this.events = events;
        }

        @NonNull
        @Override
        public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new eventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.nameText.setText(event.getName());
            holder.categoryText.setText(event.getCategory());
            holder.descriptionText.setText(event.getDescription());
            holder.dateTimeText.setText(event.getDate().toString() + " " + event.getTime().toString());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));


            holder.editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Event");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputName = new EditText(context);
                inputName.setText(event.getName());
                layout.addView(inputName);

                final EditText inputDescription = new EditText(context);
                inputDescription.setText(event.getDescription());
                layout.addView(inputDescription);

                final Spinner categorySpinner = new Spinner(context);
                ArrayAdapter<String> adapter  = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, ((ManageEvents) context).categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                categorySpinner.setSelection(((ManageEvents) context)
                        .categories.indexOf(event.getCategory()));
                layout.addView(categorySpinner);

                final EditText inputFee = new EditText(context);
                inputFee.setText(String.valueOf(event.getFee()));

                final EditText inputDate = new EditText(context);
                inputDate.setHint("Date (YYYY-MM-DD)");
                String isoDate = String.format(
                        Locale.getDefault(),
                        "%04d-%02d-%02d",
                        event.getDate().getYear(),
                        event.getDate().getMonth(),
                        event.getDate().getDay()
                );
                inputDate.setText(isoDate);
                layout.addView(inputDate);

                final EditText inputTime = new EditText(context);
                inputTime.setHint("Time (HH:MM)");
                String isoTime = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        event.getTime().getHour(),
                        event.getTime().getMinute()
                );
                inputTime.setText(isoTime);
                layout.addView(inputTime);

                builder.setView(layout);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newDesc = inputDescription.getText().toString().trim();
                    String newCat = categorySpinner.getSelectedItem().toString();
                    String newFeeStr = inputFee.getText().toString().trim();
                    String newDateStr = inputDate.getText().toString().trim();
                    String newTimeStr = inputTime.getText().toString().trim();

                    if (!newName.isEmpty() && !newDesc.isEmpty() && !newFeeStr.isEmpty()
                            && !newDateStr.isEmpty() && !newTimeStr.isEmpty()) {
                        try {
                            double newFee = Double.parseDouble(newFeeStr);

                            String[] dateParts = newDateStr.split("-");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]);
                            int day = Integer.parseInt(dateParts[2]);

                            String[] timeParts = newTimeStr.split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Date newDate = new Date(year, month, day);
                            Time newTime = new Time(hour, minute);

                            Event updatedEvent = new Event(newName, newDesc, newDate, newTime,
                                    newCat, newFee, event.getEventID(), event.getOrganizer());
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference
                                    ("events").child(event.getEventID());
                            ref.setValue(updatedEvent);

                            Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            Toast.makeText(context, "Invalid number format or date/time structure"
                                    , Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(context, "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "All fields must be filled",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete \"" + event.getName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events").child(event.getEventID());
                            ref.removeValue();
                            Toast.makeText(context,"Event Deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return events.size();
        }
    }
}