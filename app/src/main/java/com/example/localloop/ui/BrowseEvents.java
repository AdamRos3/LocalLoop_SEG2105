package com.example.localloop.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.model.Event;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Participant;
import com.example.localloop.resources.exception.InvalidJoinRequestException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BrowseEvents extends AppCompatActivity {

    private static Participant user;

    ArrayList<EventCategory> allCategories = new ArrayList<>();
    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Event> requestedEvents = new ArrayList<>();
    ArrayList<Event> joinedEvents = new ArrayList<>();
    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = (Participant)DatabaseInstance.get().getUser();

        new Thread(() -> {
            try {
                allCategories.clear();
                categoryNames.clear();
                events.clear();
                requestedEvents.clear();
                joinedEvents.clear();

                allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));

                categoryNames.add("Any Category");
                for (EventCategory category : allCategories) {
                    categoryNames.add(category.getName());
                }

                events.addAll(user.getAllEvents(DatabaseInstance.get()));
                requestedEvents.addAll(user.getJoinRequests(DatabaseInstance.get()));
                joinedEvents.addAll(user.getReservations(DatabaseInstance.get()));

                runOnUiThread(() -> {
                    Spinner categorySpinner = (Spinner) findViewById(R.id.category_dropdown);

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, categoryNames);

                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryAdapter);


                    RecyclerView eventListView = findViewById(R.id.listViewEvents);
                    adapter = new EventAdapter(this, events, requestedEvents, joinedEvents);
                    eventListView.setLayoutManager(new LinearLayoutManager(this));
                    eventListView.setAdapter(adapter);
                });

            } catch (InterruptedException e) {
                Log.e("BrowseEvents", e.toString());
                Toast.makeText(BrowseEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(this, WelcomeParticipant.class);
        startActivity(intent);
        finish();
    }

    public void onSearchClick(View view) {
        new Thread(() -> {
            EditText searchBar = findViewById(R.id.search_bar);
            Spinner categorySpinner = findViewById(R.id.category_dropdown);

            String searchText = searchBar.getText().toString().trim();
            int categoryPosition = categorySpinner.getSelectedItemPosition();

            ArrayList<Event> categoryFilteredEvents = new ArrayList<>();

            if (categoryPosition == 0) {
                try {
                    categoryFilteredEvents.addAll(user.getAllEvents(DatabaseInstance.get()));

                } catch (InterruptedException e) {
                    runOnUiThread(() -> Toast.makeText(BrowseEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show());
                }
            } else {
                try {
                    EventCategory category = allCategories.get(categoryPosition - 1);
                    categoryFilteredEvents.addAll(user.eventSearch(DatabaseInstance.get(), category));

                } catch (NoSuchEventCategoryException e) {
                    runOnUiThread(() -> Toast.makeText(BrowseEvents.this, "Invalid category", Toast.LENGTH_SHORT).show());
                } catch (InterruptedException e) {
                    runOnUiThread(() -> Toast.makeText(BrowseEvents.this, "Failed to search", Toast.LENGTH_SHORT).show());
                }
            }

            if (!searchText.isEmpty()) {
                try {
                    ArrayList<Event> searchResult = user.eventSearch(DatabaseInstance.get(), searchText);
                    ArrayList<String> searchResultIDs = new ArrayList<>();

                    for (Event e : searchResult) {
                        searchResultIDs.add(e.getEventID());
                    }

                    events.clear();

                    for (Event e : categoryFilteredEvents) {
                        if (searchResultIDs.contains(e.getEventID())) {
                            events.add(e);
                        }
                    }
                } catch (InterruptedException e) {
                    runOnUiThread(() -> Toast.makeText(BrowseEvents.this, "Failed to search", Toast.LENGTH_SHORT).show());
                }
            } else {
                events.clear();
                events.addAll(categoryFilteredEvents);
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, descriptionText, dateTimeText, feeText;
        Button joinButton;

        public EventViewHolder(@NotNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.eventName);
            categoryText = itemView.findViewById(R.id.eventCategory);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            dateTimeText = itemView.findViewById(R.id.eventDateTime);
            feeText = itemView.findViewById(R.id.eventFee);

            joinButton = itemView.findViewById(R.id.joinButton);
        }
    }

    public static class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
        Context context;
        List<Event> eventsList;
        List<Event> requestedEventsList;
        List<Event> joinedEventsList;

        public EventAdapter(Context context, List<Event> events, List<Event> requestedEvents, List<Event> joinedEvents) {
            this.context = context;
            this.eventsList = events;
            this.requestedEventsList = requestedEvents;
            this.joinedEventsList = joinedEvents;
        }

        @NotNull
        @Override
        public BrowseEvents.EventViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event_with_button,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NotNull BrowseEvents.EventViewHolder holder, int position) {
            Event event = eventsList.get(position);
            holder.nameText.setText(event.getName());

            String categoryName = "";
            for (EventCategory category : ((BrowseEvents)context).allCategories) {
                if (category.getCategoryID().equals(event.getCategoryID())) {
                    categoryName = category.getName();
                    break;
                }
            }

            holder.categoryText.setText(categoryName);
            holder.descriptionText.setText(event.getDescription());
            holder.dateTimeText.setText(event.getDate().toString() + " " + event.getTime().toString());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));

            Button joinButton = holder.joinButton;

            boolean isRequested = false;

            for (Event requestedEvent : requestedEventsList) {
                if (requestedEvent.getEventID().equals(event.getEventID())) {
                    isRequested = true;
                    break;
                }
            }

            if (isRequested) {
                joinButton.setText("Request Sent");
                joinButton.setBackgroundColor(Color.GRAY);
                joinButton.setTextColor(Color.DKGRAY);
                joinButton.setEnabled(false);
            } else {
                boolean joined = false;

                for (Event joinedEvent : joinedEventsList) {
                    if (joinedEvent.getEventID().equals(event.getEventID())) {
                        joined = true;
                        break;
                    }
                }

                if (joined) {
                    joinButton.setText("Joined");
                    joinButton.setBackgroundColor(context.getResources().getColor(R.color.green, context.getTheme()));
                    joinButton.setTextColor(Color.GRAY);
                    joinButton.setEnabled(false);
                } else {
                    joinButton.setText("Request to Join");
                    joinButton.setBackgroundColor(context.getResources().getColor(R.color.purple, context.getTheme()));
                    joinButton.setTextColor(context.getResources().getColor(R.color.white, context.getTheme()));
                    joinButton.setEnabled(true);
                }
            }

            joinButton.setOnClickListener(v -> {
                new Thread(() -> {

                    try {
                        user.requestJoinEvent(DatabaseInstance.get(), event);

                        requestedEventsList.clear();
                        requestedEventsList.addAll(user.getJoinRequests(DatabaseInstance.get()));

                        ((BrowseEvents) context).runOnUiThread(() -> {
                            ((BrowseEvents) context).adapter.notifyDataSetChanged();
                            Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();
                        });
                    } catch (InvalidJoinRequestException e) {
                        ((BrowseEvents) context).runOnUiThread(() -> Toast.makeText(context, "Invalid join request", Toast.LENGTH_SHORT).show());
                    } catch (NoSuchEventException e) {
                        ((BrowseEvents) context).runOnUiThread(() -> Toast.makeText(context, "Event does not exist", Toast.LENGTH_SHORT).show());
                    } catch (InterruptedException e) {
                        ((BrowseEvents) context).runOnUiThread(() -> Toast.makeText(context, "Failed to make join request", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });
        }

        @Override
        public int getItemCount() { return eventsList.size(); }
    }
}