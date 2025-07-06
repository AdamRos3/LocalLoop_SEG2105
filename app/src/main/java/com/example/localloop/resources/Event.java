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
    private double fee;
    private String categoryID;
    private String organizerID;
    private String eventID;

    public Event() {}

    public Event(String name, String description, Date date, Time time,
                 double fee, String categoryID, String organizerID,  String eventID) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.fee = fee;
        this.categoryID = categoryID;
        this.organizerID = organizerID;
        this.eventID = eventID;
    }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Date getDate() { return date; }

    public Time getTime() { return time; }

    public double getFee() { return fee; }

    public String getCategoryID() { return categoryID; }

    public String getOrganizerID() { return organizerID; }

    public String getEventID() { return eventID; }
}