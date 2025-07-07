package com.example.localloop.backend;

import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;

public class Event {
    private String name;
    private String description;
    private String categoryID;
    private double fee;
    private Date date;
    private Time time;
    private String organizerID;
    private String eventID;
    public Event() {
        // Empty constructor is required by Firebase
    }
    public Event(String name, String description, String categoryID, double fee, Date date, Time time, String organizerID, String eventID) {
        this.name = name;
        this.description = description;
        this.categoryID = categoryID;
        this.fee = fee;
        this.date = date;
        this.time = time;
        this.organizerID = organizerID;
        this.eventID = eventID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategoryID() {
        return categoryID;
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
    public String getOrganizerID() {
        return organizerID;
    }
    public String getEventID() {
        return eventID;
    }
    public String toString() {
        return name+", "+eventID;
    }
}
