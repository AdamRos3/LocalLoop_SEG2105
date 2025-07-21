package com.example.localloop.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.model.DatabaseConnection;
import com.example.localloop.model.Event;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Participant;
import com.example.localloop.resources.exception.NoSuchRequestException;
import com.example.localloop.resources.exception.NoSuchReservationException;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class WelcomeParticipant extends AppCompatActivity {

    private static Participant user;

    ArrayList<EventCategory> allCategories = new ArrayList<>();
    ArrayList<Event> requestedEvents = new ArrayList<>();
    ArrayList<Event> joinedEvents = new ArrayList<>();

    EventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_participant);

        // Set window insets to maintain future layout compatibility
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.returnToLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = (Participant)DatabaseInstance.get().getUser();
        String username = user.getUsername();

        // Set welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message3);
        String message = "Welcome " + username;
        welcomeMessage.setText(message);

        new Thread(() -> {
            try {
                allCategories.clear();
                requestedEvents.clear();
                joinedEvents.clear();

                allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                requestedEvents.addAll(user.getJoinRequests(DatabaseInstance.get()));
                joinedEvents.addAll(user.getReservations(DatabaseInstance.get()));

                runOnUiThread(() -> {
                    RecyclerView eventsListView = findViewById(R.id.participantEvents_recycler_view);
                    adapter = new EventAdapter(this, requestedEvents, joinedEvents);

                    eventsListView.setLayoutManager(new LinearLayoutManager(this));
                    eventsListView.setAdapter(adapter);
                });
            } catch (InterruptedException e) {
                Log.e("BrowseEvents", e.toString());
                runOnUiThread(() -> Toast.makeText(WelcomeParticipant.this, "Failed to load events", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void ReturnToLogin(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void toBrowseEvents(View view) {
        Intent intent = new Intent(this, BrowseEvents.class);
        startActivity(intent);
        finish();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, descriptionText, dateTimeText, feeText;
        TextView eventOrganizer;
        Button statusButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.eventName);
            categoryText = itemView.findViewById(R.id.eventCategory);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            dateTimeText = itemView.findViewById(R.id.eventDateTime);
            feeText = itemView.findViewById(R.id.eventFee);
            eventOrganizer = itemView.findViewById(R.id.eventOrganizer);

            statusButton = itemView.findViewById(R.id.joinButton);
        }
    }

    public static class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {
        Context context;
        List<Event> requestedEvents;
        List<Event> joinedEvents;
        List<Event> allParticipantEvents;

        public EventAdapter(Context context, List<Event> requestedEvents, List<Event> joinedEvents) {
            this.context = context;
            this.requestedEvents = requestedEvents;
            this.joinedEvents = joinedEvents;

            allParticipantEvents = new ArrayList<>();
            allParticipantEvents.addAll(joinedEvents);
            allParticipantEvents.addAll(requestedEvents);
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event_with_button,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = allParticipantEvents.get(position);
            holder.nameText.setText(event.getName());

            String categoryName = "";
            for (EventCategory category : ((WelcomeParticipant) context).allCategories) {
                if (category.getCategoryID().equals(event.getCategoryID())) {
                    categoryName = category.getName();
                    break;
                }
            }

            holder.categoryText.setText(categoryName);
            holder.descriptionText.setText(event.getDescription());
            holder.dateTimeText.setText(event.getDate().toString() + " " + event.getTime().toString());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));
            holder.eventOrganizer.setText("Organizer: loading…");
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child("Organizer")
                    .child(event.getOrganizerID())
                    .child("username")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snap) {
                            String name = snap.exists()
                                    ? snap.getValue(String.class)
                                    : "unknown";
                            holder.eventOrganizer.setText("Organizer: " + name);
                        }
                        @Override
                        public void onCancelled(DatabaseError err) {
                            holder.eventOrganizer.setText("Organizer: unknown");
                        }
                    });
            Button statusButton = holder.statusButton;

            if (joinedEvents.contains(event)) {
                statusButton.setText("Joined");
                statusButton.setBackgroundColor(context.getResources().getColor(R.color.green, context.getTheme()));
            } else if (requestedEvents.contains(event)) {
                statusButton.setText("Request Sent");
                statusButton.setBackgroundColor(Color.GRAY);
            }

            statusButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setTitle("Drop Event")
                        .setMessage("Are you sure you want to leave \"" + event.getName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    if (joinedEvents.contains(event)) {
                                        user.cancelReservation(DatabaseInstance.get(), event);

                                        ((WelcomeParticipant) context).joinedEvents.clear();
                                        ((WelcomeParticipant) context).joinedEvents.addAll(user.getReservations(DatabaseInstance.get()));

                                        allParticipantEvents.clear();
                                        allParticipantEvents.addAll(((WelcomeParticipant) context).joinedEvents);
                                        allParticipantEvents.addAll(((WelcomeParticipant) context).requestedEvents);

                                        ((WelcomeParticipant) context).runOnUiThread(() -> {
                                            ((WelcomeParticipant) context).adapter.notifyDataSetChanged();
                                            Toast.makeText(context, "Reservation removed", Toast.LENGTH_SHORT).show();
                                        });

                                    } else if (requestedEvents.contains(event)) {
                                        user.cancelJoinRequest(DatabaseInstance.get(), event);

                                        ((WelcomeParticipant) context).requestedEvents.clear();
                                        ((WelcomeParticipant) context).requestedEvents.addAll(user.getJoinRequests(DatabaseInstance.get()));

                                        allParticipantEvents.clear();
                                        allParticipantEvents.addAll(((WelcomeParticipant) context).joinedEvents);
                                        allParticipantEvents.addAll(((WelcomeParticipant) context).requestedEvents);

                                        ((WelcomeParticipant) context).runOnUiThread(() -> {
                                            ((WelcomeParticipant) context).adapter.notifyDataSetChanged();
                                            Toast.makeText(context, "Reservation removed", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    ((WelcomeParticipant) context).runOnUiThread(() -> Toast.makeText(context, "Failed to drop event", Toast.LENGTH_SHORT).show());
                                } catch (NoSuchRequestException e) {
                                    ((WelcomeParticipant) context).runOnUiThread(() -> Toast.makeText(context, "Request does not exist", Toast.LENGTH_SHORT).show());
                                } catch (NoSuchReservationException e) {
                                    ((WelcomeParticipant) context).runOnUiThread(() -> Toast.makeText(context, "Reservation does not exist", Toast.LENGTH_SHORT).show());
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            return allParticipantEvents.size();
        }
    }
}