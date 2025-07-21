package com.example.localloop.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.Event;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Organizer;
import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidDateException;
import com.example.localloop.resources.exception.InvalidTimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.Calendar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


public class WelcomeOrganizer extends AppCompatActivity {
    private static Organizer user;

    private ArrayList<EventCategory> allCategories = new ArrayList<>();
    private ArrayList<String> allCategoryNames = new ArrayList<>();
    private ArrayList<Event> userEvents = new ArrayList<>();

    private EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_organizer);

        // Set window insets to maintain future layout compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.returnToLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        user = (Organizer)DatabaseInstance.get().getUser();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        String message = "Welcome " + user.getUsername();
        welcomeMessage.setText(message);

        new Thread(() -> {
            try {
                allCategories.clear();
                userEvents.clear();
                allCategoryNames.clear();
                allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                userEvents.addAll(user.getUserEvents(DatabaseInstance.get()));
                for (EventCategory category : allCategories) {
                    allCategoryNames.add(category.getName());
                }
                runOnUiThread(() -> {
                    RecyclerView eventsListView = findViewById(R.id.listViewEvents);
                    adapter = new EventAdapter(this, userEvents);
                    eventsListView.setLayoutManager(new LinearLayoutManager(this));
                    eventsListView.setAdapter(adapter);
                });
            } catch (InterruptedException e) {
                Log.e("ManageEvents", e.toString());
                Toast.makeText(WelcomeOrganizer.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public void returnToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
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




        final TextInputEditText inputDate = new TextInputEditText(this);
        inputDate.setHint("Pick a date");
        inputDate.setFocusable(false);
        layout.addView(inputDate);

        inputDate.setOnClickListener(v1 -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                inputDate.setText(String.format("%02d/%02d/%d", day, month, year));
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        final TextInputEditText inputTime = new TextInputEditText(this);
        inputTime.setHint("Pick a time");
        inputTime.setFocusable(false);
        layout.addView(inputTime);

        inputTime.setOnClickListener(v1 -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Time")
                    .build();

            timePicker.addOnPositiveButtonClickListener( selection -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                inputTime.setText(String.format("%02d:%02d", hour, minute));
            });

            timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        });



        /*final EditText inputDate = new EditText(this);
        inputDate.setHint("Date (YYYY-MM-DD)");
        layout.addView(inputDate);

        final EditText inputTime = new EditText(this);
        inputTime.setHint("Time HH:MM");
        layout.addView(inputTime);the
         */






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
                    String[] d = dateStr.split("-");
                    user.createEvent(DatabaseInstance.get(), new Event(name, description, categoryID, fee,
                            new Date(Integer.parseInt(d[0]), (Integer.parseInt(d[1])), Integer.parseInt(d[2])),
                            new Time(Integer.parseInt(t[0]), Integer.parseInt(t[1])), user.getUserID(), null));
                    allCategories.clear();
                    userEvents.clear();
                    allCategoryNames.clear();
                    allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                    userEvents.addAll(user.getUserEvents(DatabaseInstance.get()));
                    for (EventCategory category : allCategories) {
                        allCategoryNames.add(category.getName());
                    }
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                    });
                } catch (InvalidTimeException e) {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show());
                } catch (InvalidDateException e) {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Invalid entry", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.show();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView nameText, categoryText, descriptionText, dateTimeText, feeText;
        ImageButton userButton, editButton, deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.eventName);
            categoryText = itemView.findViewById(R.id.eventCategory);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            dateTimeText = itemView.findViewById(R.id.eventDateTime);
            feeText = itemView.findViewById(R.id.eventFee);

            userButton = itemView.findViewById(R.id.buttonUsers);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public static class EventAdapter extends RecyclerView.Adapter<WelcomeOrganizer.EventViewHolder> {

        Context context;
        List<Event> events;

        public EventAdapter(Context context, List<Event> events) {
            this.context = context;
            this.events = events;
        }

        @NonNull
        @Override
        public WelcomeOrganizer.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WelcomeOrganizer.EventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_manage_event,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WelcomeOrganizer.EventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.nameText.setText(event.getName());

            String categoryName = "";
            for (EventCategory category : ((WelcomeOrganizer)context).allCategories) {
                if (category.getCategoryID().equals(event.getCategoryID())) {
                    categoryName = category.getName();
                    break;
                }
            }

            holder.categoryText.setText(categoryName);
            holder.descriptionText.setText(event.getDescription());
            holder.dateTimeText.setText(event.getDate().toString() + " " + event.getTime().toString());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));

            holder.userButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ManageParticipants.class);
                intent.putExtra("eventID", event.getEventID());
                context.startActivity(intent);
            });

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
                        android.R.layout.simple_spinner_item, ((WelcomeOrganizer)context).allCategoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
                categorySpinner.setSelection(((WelcomeOrganizer) context)
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
                    String newCatID = ((WelcomeOrganizer)context).allCategories.get(newCatPos).getCategoryID();


                    if (!newName.isEmpty() && !newDesc.isEmpty() && !newFeeStr.isEmpty()
                            && !newDateStr.isEmpty() && !newTimeStr.isEmpty()) {
                        new Thread(() -> {
                            double newFee = Double.parseDouble(newFeeStr);

                            String[] dateParts = newDateStr.split("-");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = (Integer.parseInt(dateParts[1]));
                            int day = Integer.parseInt(dateParts[2]);

                            String[] timeParts = newTimeStr.split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            try {
                                user.editEvent(DatabaseInstance.get(), event, new Event(newName, newDesc, newCatID, newFee, new Date(year, month, day), new Time(hour, minute), user.getUserID(), event.getEventID()));
                                ((WelcomeOrganizer) context).allCategories.clear();
                                ((WelcomeOrganizer) context).userEvents.clear();
                                ((WelcomeOrganizer) context).allCategoryNames.clear();
                                ((WelcomeOrganizer) context).allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                                ((WelcomeOrganizer) context).userEvents.addAll(user.getUserEvents(DatabaseInstance.get()));
                                for (EventCategory category : ((WelcomeOrganizer) context).allCategories) {
                                    ((WelcomeOrganizer) context).allCategoryNames.add(category.getName());
                                }
                                ((WelcomeOrganizer) context).runOnUiThread(() -> {
                                    ((WelcomeOrganizer) context).adapter.notifyDataSetChanged();
                                    Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show();
                                });
                            } catch (InvalidTimeException e) {
                                ((WelcomeOrganizer) context).runOnUiThread(() -> Toast.makeText(context, "Invalid time", Toast.LENGTH_SHORT).show());
                            } catch (InvalidDateException e) {
                                ((WelcomeOrganizer)context).runOnUiThread(() -> Toast.makeText(context, "Invalid date", Toast.LENGTH_SHORT).show());
                            } catch (Exception e) {
                                ((WelcomeOrganizer)context).runOnUiThread(() -> Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    } else {
                        ((WelcomeOrganizer)context).runOnUiThread(() -> Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show());
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
                                    user.deleteEvent(DatabaseInstance.get(),event);
                                    ((WelcomeOrganizer)context).allCategories.clear();
                                    ((WelcomeOrganizer)context).userEvents.clear();
                                    ((WelcomeOrganizer)context).allCategoryNames.clear();
                                    ((WelcomeOrganizer)context).allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                                    ((WelcomeOrganizer)context).userEvents.addAll(user.getUserEvents(DatabaseInstance.get()));
                                    for (EventCategory category:((WelcomeOrganizer)context).allCategories) {
                                        ((WelcomeOrganizer)context).allCategoryNames.add(category.getName());
                                    }
                                    ((WelcomeOrganizer)context).runOnUiThread(() -> {
                                        ((WelcomeOrganizer)context).adapter.notifyDataSetChanged();
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