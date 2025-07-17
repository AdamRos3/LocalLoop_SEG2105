package com.example.localloop.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.TokenWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.model.Event;
import com.example.localloop.model.EventCategory;
import com.example.localloop.model.Participant;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BrowseEvents extends AppCompatActivity {

    private static Participant user;

    ArrayList<EventCategory> allCategories = new ArrayList<>();
    ArrayList<String> categoryNames = new ArrayList<>();
    ArrayList<Event> events = new ArrayList<>();

//    eventAdapter adapter;

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
                events.clear();

                allCategories.addAll(user.getAllEventCategories(DatabaseInstance.get()));
                for (EventCategory category : allCategories) {
                    categoryNames.add(category.getName());
                }

                events.addAll(user.getAllEvents(DatabaseInstance.get()));

                runOnUiThread(() -> {
                    RecyclerView eventListView = findViewById(R.id.listViewEvents);
                });

            } catch (InterruptedException e) {
                Log.e("BrowseEvents", e.toString());
                Toast.makeText(BrowseEvents.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackClick(View view) { finish(); }

    public static class eventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, categoryText, descriptionText, dateTimeText, feeText;
        Button joinButton;

        public eventViewHolder(@NotNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.eventName);
            categoryText = itemView.findViewById(R.id.eventCategory);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            dateTimeText = itemView.findViewById(R.id.eventDateTime);
            feeText = itemView.findViewById(R.id.eventFee);

            joinButton = itemView.findViewById(R.id.joinButton);
        }
    }

    public static class eventAdapter extends RecyclerView.Adapter<eventViewHolder> {
        Context context;
        List<Event> events;

        public eventAdapter (Context context, List<Event> events) {
            this.context = context;
            this.events = events;
        }

        @NotNull
        @Override
        public eventViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            return new eventViewHolder(LayoutInflater.from(context).inflate(R.layout.item_browse_event,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NotNull eventViewHolder holder, int position) {
            Event event = events.get(position);
            holder.nameText.setText(event.getName());

            String categoryName = "";
            for (EventCategory category : ((BrowseEvents)context).allCategories) {
                if (category.getCategoryID().equals(event.getCategoryID())) {
                    categoryName = category.getName();
                    break;
                }
            }

            holder.categoryText.setText(categoryName);
            holder.descriptionText.setText(event.getDate().toString() + " " + event.getTime().toString());
            holder.feeText.setText(String.format(Locale.getDefault(), "%.2f", event.getFee()));

            holder.joinButton.setOnClickListener(v -> {

            });
        }

        //TODO: this doesn't reflect the number of items that will appear as events the user is already enrolled in will not appear
        @Override
        public int getItemCount() { return events.size(); }
    }
}