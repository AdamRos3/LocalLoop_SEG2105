package com.example.localloop.resources;

import com.example.localloop.manageCategory;

import java.util.Dictionary;

import java.util.ArrayList;

import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;

public class Event {
    private String name;
    private String description;
    private Date date;
    private Time time;
    private String category;
    private double fee;
    private Organizer organizer;


    String eventID;

    public Event() {}

    public Event(String name, String description, Date date, Time time,
                 String category, double fee, String eventID, Organizer organizer) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
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

    public Date getDate() {
        return date;
    }
    public Time getTime() { return time; }

    public String getCategory() { return category; }

    public double getFee() { return fee; }

    public String getEventID() { return eventID; }

    public Organizer getOrganizer() { return organizer; }
}
