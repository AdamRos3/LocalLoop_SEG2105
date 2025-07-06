package com.example.localloop.backend;

import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;

public class Event {
    private String name;
    private String description;
    private EventCategory category;
    private double fee;
    private Date date;
    private Time time;
    private String eventID;
    public Event() {
        // Empty constructor is required by Firebase
    }
    public Event(String name, String description, EventCategory category, double fee, Date date, Time time, String eventID) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.fee = fee;
        this.date = date;
        this.time = time;
        this.eventID = eventID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public EventCategory getCategory() {
        return category;
    }
    public double getFee() {
        return fee;
    }
    public Date getDate() {
        return date;
    }
    public Time getTime() {
        return time;
    }
    public String getEventID() {
        return eventID;
    }
    public String toString() {
        return name+", "+eventID;
    }
}
