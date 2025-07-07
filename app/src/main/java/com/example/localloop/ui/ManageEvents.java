package com.example.localloop.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.Event;
import com.example.localloop.backend.EventCategory;
import com.example.localloop.backend.Organizer;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class ManageEvents extends AppCompatActivity {

    private Organizer organizer;
    private ArrayList<EventCategory> allCategories = new ArrayList<>();
    private ArrayList<Event> userEvents = new ArrayList<>();
    private eventAdapter adapter;
    private DatabaseConnection dbConnection;

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

        dbConnection = DatabaseInstance.get();
        organizer = (Organizer) dbConnection.getUser();

        RecyclerView eventsListView = findViewById(R.id.listViewEvents);
        adapter = new eventAdapter(this, userEvents);
        eventsListView.setLayoutManager(new LinearLayoutManager(this));
        eventsListView.setAdapter(adapter);

        fetchAndRefreshData();
    }

    private void fetchAndRefreshData() {
        new Thread(() -> {
            try {
                ArrayList<Event> userEvents = organizer.getUserEvents(dbConnection);
                ArrayList<EventCategory> userCategories = organizer.getAllEventCategories(dbConnection);

                runOnUiThread(() -> {
                    this.userEvents.clear();
                    this.userEvents.addAll(userEvents);
                    this.allCategories.clear();
                    this.allCategories.addAll(userCategories);
                    adapter.notifyDataSetChanged();
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
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
        layout.addView(categorySpinner);

        // Populate spinner with category names (Strings)
        ArrayList<String> userCategoriesNames = new ArrayList<>();
        for (EventCategory c : allCategories) {
            userCategoriesNames.add(c.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userCategoriesNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        final EditText inputFee = new EditText(this);
        inputFee.setHint("Fee");
        inputFee.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputFee);

        // Date inputs
        final EditText inputYear = new EditText(this);
        inputYear.setHint("Year (e.g., 2025)");
        inputYear.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputYear);

        final EditText inputMonth = new EditText(this);
        inputMonth.setHint("Month (1-12)");
        inputMonth.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputMonth);

        final EditText inputDay = new EditText(this);
        inputDay.setHint("Day (1-31)");
        inputDay.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputDay);

        // Time inputs
        final EditText inputHour = new EditText(this);
        inputHour.setHint("Hour (0-23)");
        inputHour.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputHour);

        final EditText inputMinute = new EditText(this);
        inputMinute.setHint("Minute (0-59)");
        inputMinute.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputMinute);

        // Timezone spinner
        final Spinner timezoneSpinner = new Spinner(this);
        ArrayAdapter<String> timezoneAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"EST", "CST", "MST", "PST", "GMT", "UTC"});
        timezoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timezoneSpinner.setAdapter(timezoneAdapter);
        layout.addView(timezoneSpinner);

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();
            String selectedCategoryName = (String) categorySpinner.getSelectedItem();
            String feeStr = inputFee.getText().toString().trim();

            String yearStr = inputYear.getText().toString().trim();
            String monthStr = inputMonth.getText().toString().trim();
            String dayStr = inputDay.getText().toString().trim();

            String hourStr = inputHour.getText().toString().trim();
            String minuteStr = inputMinute.getText().toString().trim();
            String timezone = timezoneSpinner.getSelectedItem().toString();

            if (name.isEmpty() || description.isEmpty() || feeStr.isEmpty() ||
                    yearStr.isEmpty() || monthStr.isEmpty() || dayStr.isEmpty() ||
                    hourStr.isEmpty() || minuteStr.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            // Find categoryID by category name
            String categoryID = null;
            for (EventCategory c : allCategories) {
                if (c.getName().equals(selectedCategoryName)) {
                    categoryID = c.getCategoryID();
                    break;
                }
            }
            if (categoryID == null) {
                Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double fee = Double.parseDouble(feeStr);
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);

                Date date = new Date(year, month, day);
                Time time = new Time(hour, minute, timezone);

                Event newEvent = new Event(name, description, categoryID, fee, date, time, organizer.getUserID(), null);

                // Perform backend operation in separate thread
                new Thread(() -> {
                    try {
                        organizer.createEvent(dbConnection, newEvent);

                        runOnUiThread(() -> {
                            userEvents.add(newEvent);
                            adapter.notifyItemInserted(userEvents.size() - 1);
                            Toast.makeText(this, "Event Added", Toast.LENGTH_SHORT).show();
                        });
                    } catch (NoSuchEventCategoryException | InvalidEventNameException | InterruptedException e) {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to add event: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numeric values for fee, date, and time", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    public static class eventViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, categoryText, descriptionText, feeText;
        TextView yearText, monthText, dayText, hourText, minuteText, timezoneText;
        ImageButton editButton, deleteButton;

        public eventViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name);
            categoryText = itemView.findViewById(R.id.category);
            descriptionText = itemView.findViewById(R.id.description);
            feeText = itemView.findViewById(R.id.fee);

            yearText = itemView.findViewById(R.id.year);
            monthText = itemView.findViewById(R.id.month);
            dayText = itemView.findViewById(R.id.day);
            hourText = itemView.findViewById(R.id.hour);
            minuteText = itemView.findViewById(R.id.minute);
            timezoneText = itemView.findViewById(R.id.timezone);

            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public class eventAdapter extends RecyclerView.Adapter<eventViewHolder> {

        Context context;
        List<Event> events;

        public eventAdapter(Context context, List<Event> events) {
            this.context = context;
            this.events = events;
        }

        @NonNull
        @Override
        public eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new eventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {
            Event event = events.get(position);

            holder.nameText.setText(event.getName());

            EventCategory selectedCategory = null;
            for (EventCategory c : allCategories) {
                if (event.getCategoryID().equals(c.getCategoryID())) {
                    selectedCategory = c;
                    break;
                }
            }
            holder.categoryText.setText(selectedCategory != null ? selectedCategory.getName() : "Unknown");

            holder.descriptionText.setText(event.getDescription());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));

            if (event.getDate() != null) {
                holder.yearText.setText(String.valueOf(event.getDate().getYear()));
                holder.monthText.setText(String.valueOf(event.getDate().getMonth()));
                holder.dayText.setText(String.valueOf(event.getDate().getDay()));
            } else {
                holder.yearText.setText("");
                holder.monthText.setText("");
                holder.dayText.setText("");
            }

            if (event.getTime() != null) {
                holder.hourText.setText(String.valueOf(event.getTime().getHour()));
                holder.minuteText.setText(String.valueOf(event.getTime().getMinute()));
                holder.timezoneText.setText(event.getTime().getTimezone());
            } else {
                holder.hourText.setText("");
                holder.minuteText.setText("");
                holder.timezoneText.setText("");
            }

            holder.editButton.setOnClickListener(v -> {
                Event eventToEdit = events.get(holder.getAdapterPosition());

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("Edit Event");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputName = new EditText(context);
                inputName.setHint("Name");
                inputName.setText(eventToEdit.getName());
                layout.addView(inputName);

                final EditText inputDescription = new EditText(context);
                inputDescription.setHint("Description");
                inputDescription.setText(eventToEdit.getDescription());
                layout.addView(inputDescription);

                final Spinner categorySpinner = new Spinner(context);
                ArrayList<String> categoryNames = new ArrayList<>();
                for (EventCategory c : allCategories) {
                    categoryNames.add(c.getName());
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
                for (int i = 0; i < allCategories.size(); i++) {
                    if (allCategories.get(i).getCategoryID().equals(eventToEdit.getCategoryID())) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }
                layout.addView(categorySpinner);

                final EditText inputFee = new EditText(context);
                inputFee.setHint("Fee");
                inputFee.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                inputFee.setText(String.valueOf(eventToEdit.getFee()));
                layout.addView(inputFee);

                final EditText inputYear = new EditText(context);
                inputYear.setHint("Year");
                inputYear.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                inputYear.setText(String.valueOf(eventToEdit.getDate().getYear()));
                layout.addView(inputYear);

                final EditText inputMonth = new EditText(context);
                inputMonth.setHint("Month");
                inputMonth.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                inputMonth.setText(String.valueOf(eventToEdit.getDate().getMonth()));
                layout.addView(inputMonth);

                final EditText inputDay = new EditText(context);
                inputDay.setHint("Day");
                inputDay.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                inputDay.setText(String.valueOf(eventToEdit.getDate().getDay()));
                layout.addView(inputDay);

                final EditText inputHour = new EditText(context);
                inputHour.setHint("Hour");
                inputHour.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                inputHour.setText(String.valueOf(eventToEdit.getTime().getHour()));
                layout.addView(inputHour);

                final EditText inputMinute = new EditText(context);
                inputMinute.setHint("Minute");
                inputMinute.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                inputMinute.setText(String.valueOf(eventToEdit.getTime().getMinute()));
                layout.addView(inputMinute);

                final Spinner timezoneSpinner = new Spinner(context);
                String[] timezones = {"EST", "CST", "MST", "PST", "GMT", "UTC"};
                ArrayAdapter<String> timezoneAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timezones);
                timezoneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timezoneSpinner.setAdapter(timezoneAdapter);
                for (int i = 0; i < timezones.length; i++) {
                    if (timezones[i].equals(eventToEdit.getTime().getTimezone())) {
                        timezoneSpinner.setSelection(i);
                        break;
                    }
                }
                layout.addView(timezoneSpinner);

                dialogBuilder.setView(layout);

                dialogBuilder.setPositiveButton("Save", (dialog, which) -> {
                    String newName = inputName.getText().toString().trim();
                    String newDescription = inputDescription.getText().toString().trim();
                    String newCategoryName = (String) categorySpinner.getSelectedItem();
                    String newFeeStr = inputFee.getText().toString().trim();
                    String newYearStr = inputYear.getText().toString().trim();
                    String newMonthStr = inputMonth.getText().toString().trim();
                    String newDayStr = inputDay.getText().toString().trim();
                    String newHourStr = inputHour.getText().toString().trim();
                    String newMinuteStr = inputMinute.getText().toString().trim();
                    String newTimezone = timezoneSpinner.getSelectedItem().toString();

                    if (newName.isEmpty() || newDescription.isEmpty() || newFeeStr.isEmpty() ||
                            newYearStr.isEmpty() || newMonthStr.isEmpty() || newDayStr.isEmpty() ||
                            newHourStr.isEmpty() || newMinuteStr.isEmpty()) {
                        Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AtomicReference<String> newCategoryIDRef = new AtomicReference<>(null);
                    for (EventCategory c : allCategories) {
                        if (c.getName().equals(newCategoryName)) {
                            newCategoryIDRef.set(c.getCategoryID());
                            break;
                        }
                    }

                    String newCategoryID = newCategoryIDRef.get();
                    if (newCategoryID == null) {
                        Toast.makeText(context, "Invalid category selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double newFee = Double.parseDouble(newFeeStr);
                        int newYear = Integer.parseInt(newYearStr);
                        int newMonth = Integer.parseInt(newMonthStr);
                        int newDay = Integer.parseInt(newDayStr);
                        int newHour = Integer.parseInt(newHourStr);
                        int newMinute = Integer.parseInt(newMinuteStr);

                        Date newDate = new Date(newYear, newMonth, newDay);
                        Time newTime = new Time(newHour, newMinute, newTimezone);

                        new Thread(() -> {
                            try {
                                organizer.editEvent(dbConnection, eventToEdit, newName, newDescription, newCategoryID, newFee, newDate, newTime);
                                runOnUiThread(() -> {
                                    notifyItemChanged(holder.getAdapterPosition());
                                    fetchAndRefreshData();
                                    Toast.makeText(context, "Event updated", Toast.LENGTH_SHORT).show();
                                });
                            } catch (InterruptedException | NoSuchEventException | NoSuchEventCategoryException e) {
                                runOnUiThread(() -> Toast.makeText(context, "Failed to update event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } catch (InvalidEventNameException e) {
                                runOnUiThread(() -> Toast.makeText(context, "Failed to update event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).start();

                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
                    }
                });

                dialogBuilder.setNegativeButton("Cancel", null);
                dialogBuilder.show();
            });


            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete \"" + event.getName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int pos = holder.getAdapterPosition();
                            if (pos == RecyclerView.NO_POSITION) return; // safeguard

                            new Thread(() -> {
                                try {
                                    organizer.deleteEvent(dbConnection, event);
                                    runOnUiThread(() -> {
                                        Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
                                        events.remove(pos);
                                        notifyItemRemoved(pos);
                                    });
                                } catch (NoSuchEventException e) {
                                    runOnUiThread(() -> Toast.makeText(context, "Event Cannot be Deleted", Toast.LENGTH_SHORT).show());
                                } catch (InterruptedException e) {
                                    runOnUiThread(() -> Toast.makeText(context, "Event Cannot be Deleted", Toast.LENGTH_SHORT).show());
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