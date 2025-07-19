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
import com.example.localloop.model.Event;
import com.example.localloop.model.Organizer;
import com.example.localloop.model.Participant;
import com.example.localloop.resources.exception.NoSuchEventException;

import java.util.ArrayList;
import java.util.List;

public class ManageParticipants extends AppCompatActivity {

    private static Organizer organizer;
    private static Event event;

    private ArrayList<Participant> requestingParticipants = new ArrayList<>();
    private ArrayList<Participant> enrolledParticipants = new ArrayList<>();
    private ManageParticipants.participantRequestsAdapter requestsAdapter;
    private ManageParticipants.participantEnrolledAdapter enrolledAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_participants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        organizer = (Organizer) DatabaseInstance.get().getUser();
        String eventID = getIntent().getStringExtra("eventID");

        new Thread(() -> {
            try {
                event = DatabaseInstance.get().getEventFromID(eventID);
            } catch (InterruptedException e) {
                Log.e("ManageParticipants", e.toString());
                runOnUiThread(() -> Toast.makeText(ManageParticipants.this, "Failed to retrieve event", Toast.LENGTH_SHORT).show());
            } catch (NoSuchEventException e) {
                Log.e("ManageParticipants", e.toString());
                runOnUiThread(() -> Toast.makeText(ManageParticipants.this, "Event does not exist", Toast.LENGTH_SHORT).show());
            }

            try {
                requestingParticipants.clear();
                enrolledParticipants.clear();

                requestingParticipants.addAll(organizer.getJoinRequests(DatabaseInstance.get(), event));
                enrolledParticipants.addAll(organizer.getReservations(DatabaseInstance.get(), event));

                runOnUiThread(() -> {
                    RecyclerView requestsListView = findViewById(R.id.listViewRequests);
                    requestsAdapter = new participantRequestsAdapter(this, requestingParticipants);
                    requestsListView.setLayoutManager(new LinearLayoutManager(this));
                    requestsListView.setAdapter(requestsAdapter);

                    RecyclerView enrolledListView = findViewById(R.id.listViewEnrolledParticipants);
                    enrolledAdapter = new participantEnrolledAdapter(this, enrolledParticipants);
                    enrolledListView.setLayoutManager(new LinearLayoutManager(this));
                    enrolledListView.setAdapter(enrolledAdapter);
                });
            }
            catch (InterruptedException e) {
                Log.e("ManageParticipants", e.toString());
                runOnUiThread(() -> Toast.makeText(ManageParticipants.this, "Failed to load events", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void onBackClick(View view) { finish(); }

    public static class participantRequestsViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;

        ImageButton acceptButton, rejectButton;

        public participantRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            participantName = itemView.findViewById(R.id.participantName);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }

    public static class participantEnrolledViewHolder extends RecyclerView.ViewHolder {
        TextView participantName;

        ImageButton removeButton;

        public participantEnrolledViewHolder(@NonNull View itemView) {
            super(itemView);

            participantName = itemView.findViewById(R.id.participantName);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public static class participantRequestsAdapter extends RecyclerView.Adapter<participantRequestsViewHolder> {
        Context context;
        List<Participant> requests;

        public participantRequestsAdapter(Context context, List<Participant> requests) {
            this.context = context;
            this.requests = requests;
        }

        @NonNull
        @Override
        public participantRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new participantRequestsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_requesting_participant,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull participantRequestsViewHolder holder, int position) {
            Participant requestingParticipant = requests.get(position);

            holder.participantName.setText(requestingParticipant.getUsername());

            holder.acceptButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Accept Request")
                        .setMessage("Are you sure you want to accept \"" + requestingParticipant.getUsername() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    organizer.acceptJoinRequest(DatabaseInstance.get(), requestingParticipant, event);

                                    ((ManageParticipants) context).requestingParticipants.clear();
                                    ((ManageParticipants) context).enrolledParticipants.clear();

                                    for (Participant p : organizer.getJoinRequests(DatabaseInstance.get(), event)) {
                                        ((ManageParticipants) context).requestingParticipants.add(p);
                                    }

                                    for (Participant p : organizer.getReservations(DatabaseInstance.get(), event)) {
                                        ((ManageParticipants) context).enrolledParticipants.add(p);
                                    }

                                    ((ManageParticipants) context).runOnUiThread(() -> {
                                        ((ManageParticipants) context).requestsAdapter.notifyDataSetChanged();
                                        ((ManageParticipants) context).enrolledAdapter.notifyDataSetChanged();
                                        Toast.makeText(context, "Participant Accepted", Toast.LENGTH_SHORT).show();
                                    });
                                } catch (InterruptedException e) {
                                    Toast.makeText(context, "Error cannot accept request", Toast.LENGTH_SHORT).show();
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            holder.rejectButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Deny Request")
                        .setMessage("Are you sure you want to reject \"" + requestingParticipant.getUsername() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    organizer.rejectJoinRequest(DatabaseInstance.get(), requestingParticipant, event);

                                    ((ManageParticipants) context).requestingParticipants.clear();

                                    for (Participant p : organizer.getJoinRequests(DatabaseInstance.get(), event)) {
                                        ((ManageParticipants) context).requestingParticipants.add(p);
                                    }

                                    ((ManageParticipants) context).runOnUiThread(() -> {
                                        ((ManageParticipants) context).requestsAdapter.notifyDataSetChanged();
                                    });
                                } catch (InterruptedException e) {
                                    ((ManageParticipants) context).runOnUiThread(() -> Toast.makeText(context, "Error cannot reject request", Toast.LENGTH_SHORT).show());
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return requests.size(); }
    }

    public static class participantEnrolledAdapter extends RecyclerView.Adapter<participantEnrolledViewHolder> {
        Context context;
        List<Participant> enrolled;

        public participantEnrolledAdapter(Context context, List<Participant> enrolled) {
            this.context = context;
            this.enrolled = enrolled;
        }

        @NonNull
        @Override
        public participantEnrolledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new participantEnrolledViewHolder(LayoutInflater.from(context).inflate(R.layout.item_enrolled_participant,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull participantEnrolledViewHolder holder, int position) {
            Participant enrolledParticipant = enrolled.get(position);

            holder.participantName.setText(enrolledParticipant.getUsername());

            holder.removeButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Remove Participant")
                        .setMessage("Are you sure you want to remove \"" + enrolledParticipant.getUsername() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            new Thread(() -> {
                                try {
                                    organizer.removeReservations(DatabaseInstance.get(), enrolledParticipant, event);

                                    ((ManageParticipants) context).enrolledParticipants.clear();
                                    for (Participant p : organizer.getReservations(DatabaseInstance.get(), event)) {
                                        ((ManageParticipants) context).enrolledParticipants.add(p);
                                    }

                                    ((ManageParticipants) context).runOnUiThread(() -> {
                                        ((ManageParticipants) context).enrolledAdapter.notifyDataSetChanged();
                                        Toast.makeText(context, "Participant Removed", Toast.LENGTH_SHORT).show();
                                    });
                                } catch (InterruptedException e) {
                                    ((ManageParticipants) context).runOnUiThread(() -> Toast.makeText(context, "Error cannot remove participant", Toast.LENGTH_SHORT).show());
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        }

        @Override
        public  int getItemCount() { return enrolled.size(); }
    }

}