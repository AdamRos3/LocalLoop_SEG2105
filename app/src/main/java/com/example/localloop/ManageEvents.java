package com.example.localloop;

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

import com.example.localloop.resources.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageEvents extends AppCompatActivity {

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

        RecyclerView eventsListView = findViewById(R.id.listViewEvents);

        List<Event> events = new ArrayList<>();

        populateEvents(events);

        eventsListView.setLayoutManager(new LinearLayoutManager(this));
        eventsListView.setAdapter(new eventAdapter(getApplicationContext(), events));

    }

    public void onBackClick(View view) {
        finish();
    }

    public void onAddClick(View view) {
    }

    private void populateEvents(List<Event> events) {
        //Get Events from Database
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");

        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                events.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);

                    if (event != null) {
                        events.add(event);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ManageEventListener", error.toException());
            }
        });
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
            return new eventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull eventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.nameText.setText(event.getName());
            holder.categoryText.setText(event.getCategory());
            holder.descriptionText.setText(event.getDescription());
            holder.dateTimeText.setText(event.getDateTime());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));


            holder.editButton.setOnClickListener(v -> {

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