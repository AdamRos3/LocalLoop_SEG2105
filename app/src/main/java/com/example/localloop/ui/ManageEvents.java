package com.example.localloop.ui;

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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Event;
import com.example.localloop.model.Organizer;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageEvents extends AppCompatActivity {

    private static Organizer organizer;
    private ArrayList<EventCategory> allCategories = new ArrayList<>();
    private ArrayList<String> allCategoryNames = new ArrayList<>();
    private ArrayList<Event> userEvents = new ArrayList<>();
    private eventAdapter adapter;

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

        organizer = (Organizer) DatabaseInstance.get().getUser();

        new Thread(() -> {
            try {
                allCategories.clear();
                userEvents.clear();
                allCategoryNames.clear();
                allCategories.addAll(organizer.getAllEventCategories(DatabaseInstance.get()));
                userEvents.addAll(organizer.getUserEvents(DatabaseInstance.get()));
                for (EventCategory category:allCategories) {
                    allCategoryNames.add(category.getName());
                }
                runOnUiThread(() -> {
                    RecyclerView eventsListView = findViewById(R.id.listViewEvents);
                    adapter = new eventAdapter(this, userEvents);
                    eventsListView.setLayoutManager(new LinearLayoutManager(this));
                    eventsListView.setAdapter(adapter);
                });
            } catch (InterruptedException e) {
                Log.e("ManageEvents", e.toString());
                Toast.makeText(ManageEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        }).start();
        /*
        //retrieveCategoriesFromDatabase();

        RecyclerView eventsListView = findViewById(R.id.listViewEvents);
        eventAdapter adapter = new eventAdapter(this, userEvents);
        eventsListView.setLayoutManager(new LinearLayoutManager(this));
        eventsListView.setAdapter(adapter);


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
         */
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
        categorySpinner.setPrompt("EventCategory");

        layout.addView(categorySpinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, allCategoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);


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
            String dateStr = inputDate.getText().toString().trim();
            String timeStr = inputTime.getText().toString().trim();
            String feeStr = inputFee.getText().toString().trim();
            int categoryPosition = categorySpinner.getSelectedItemPosition();
            String categoryID = (allCategories.get(categoryPosition)).getCategoryID();

            if (name.isEmpty() || description.isEmpty() || feeStr.isEmpty()
                    || dateStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                try {
                    double fee = Double.parseDouble(feeStr);
                    String[] t = timeStr.split(":");
                    Time timeObj = new Time(Integer.parseInt(t[0]), Integer.parseInt(t[1]));
                    String[] d = dateStr.split("-");
                    Date dateObj = new Date(Integer.parseInt(d[0]), (Integer.parseInt(d[1]) - 1), Integer.parseInt(d[2]));
                    organizer.createEvent(DatabaseInstance.get(), new Event(name, description, categoryID, fee, dateObj, timeObj, organizer.getUserID(), null));
                    allCategories.clear();
                    userEvents.clear();
                    allCategoryNames.clear();
                    allCategories.addAll(organizer.getAllEventCategories(DatabaseInstance.get()));
                    userEvents.addAll(organizer.getUserEvents(DatabaseInstance.get()));
                    for (EventCategory category:allCategories) {
                        allCategoryNames.add(category.getName());
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid fee or date/time", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
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

            String categoryName = "";
            for (EventCategory category : ((ManageEvents)context).allCategories) {
                if (category.getCategoryID().equals(event.getCategoryID())) {
                    categoryName = category.getName();
                    break;
                }
            }

            holder.categoryText.setText(categoryName);
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
                ArrayAdapter<String> categoryAdapter  = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, ((ManageEvents)context).allCategoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
                categorySpinner.setSelection(((ManageEvents) context)
                        .allCategoryNames.indexOf(event.getCategoryID()));
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
                    String newDateStr = inputDate.getText().toString().trim();
                    String newTimeStr = inputTime.getText().toString().trim();
                    String newFeeStr = inputFee.getText().toString().trim();
                    int newCatPos = categorySpinner.getSelectedItemPosition();
                    String newCatID = ((ManageEvents)context).allCategories.get(newCatPos).getCategoryID();


                    if (!newName.isEmpty() && !newDesc.isEmpty() && !newFeeStr.isEmpty()
                            && !newDateStr.isEmpty() && !newTimeStr.isEmpty()) {
                        new Thread(() -> {
                            double newFee = Double.parseDouble(newFeeStr);

                            String[] dateParts = newDateStr.split("-");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = (Integer.parseInt(dateParts[1]) - 1);
                            int day = Integer.parseInt(dateParts[2]);

                            String[] timeParts = newTimeStr.split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Date newDate = new Date(year, month, day);
                            Time newTime = new Time(hour, minute);

                            try {
                                organizer.editEvent(DatabaseInstance.get(),event,new Event(newName, newDesc, newCatID, newFee, newDate, newTime, organizer.getUserID(), event.getEventID()));
                                ((ManageEvents)context).allCategories.clear();
                                ((ManageEvents)context).userEvents.clear();
                                ((ManageEvents)context).allCategoryNames.clear();
                                ((ManageEvents)context).allCategories.addAll(organizer.getAllEventCategories(DatabaseInstance.get()));
                                ((ManageEvents)context).userEvents.addAll(organizer.getUserEvents(DatabaseInstance.get()));
                                for (EventCategory category:((ManageEvents)context).allCategories) {
                                    ((ManageEvents)context).allCategoryNames.add(category.getName());
                                }
                                ((ManageEvents)context).runOnUiThread(() -> {
                                    ((ManageEvents)context).adapter.notifyDataSetChanged();
                                    Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception e) {
                                ((ManageEvents)context).runOnUiThread(() -> Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    } else {
                        ((ManageEvents)context).runOnUiThread(() -> Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show());
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
                            new Thread(() -> {
                                try {
                                    organizer.deleteEvent(DatabaseInstance.get(),event);
                                    ((ManageEvents)context).allCategories.clear();
                                    ((ManageEvents)context).userEvents.clear();
                                    ((ManageEvents)context).allCategoryNames.clear();
                                    ((ManageEvents)context).allCategories.addAll(organizer.getAllEventCategories(DatabaseInstance.get()));
                                    ((ManageEvents)context).userEvents.addAll(organizer.getUserEvents(DatabaseInstance.get()));
                                    for (EventCategory category:((ManageEvents)context).allCategories) {
                                        ((ManageEvents)context).allCategoryNames.add(category.getName());
                                    }
                                    ((ManageEvents)context).runOnUiThread(() -> {
                                        ((ManageEvents)context).adapter.notifyDataSetChanged();
                                    });
                                    ((AppCompatActivity)context).runOnUiThread(() -> {
                                        Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
                                    });
                                    } catch (Exception e) {
                                    Toast.makeText(context, "Error Unable to Delete Event", Toast.LENGTH_SHORT).show();
                                }
                            }).start();
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