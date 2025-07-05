package com.example.localloop.resources;

import com.example.localloop.manageCategory;

import java.util.Dictionary;

import java.util.ArrayList;

public class Event {
    String name;
    String description;
    String dateTime;
    String category;
    double fee;
    Organizer organizer;

    String eventID;

    public Event() {}

    public Event(String name, String description, String dateTime,
                 String category, double fee, String eventID, Organizer organizer) {
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.category = category;
        this.fee = fee;
        this.eventID = eventID;
        this.organizer = organizer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getCategory() { return category; }

    public double getFee() { return fee; }

    public String getEventID() { return eventID; }

    public Organizer getOrganizer() { return organizer; }
}
