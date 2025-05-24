package com.example.localloop.resources;

import java.util.ArrayList;

public class Events {
    private String title;
    private String description;
    private String dateTime;
    private String location;
    private String category;
    private int maxParticipants;
    private ArrayList<String> registeredUsers;

    public Events(String title, String description, String dateTime, String location,
                 String category, int maxParticipants) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.category = category;
        this.maxParticipants = maxParticipants;
        this.registeredUsers = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public ArrayList<String> getRegisteredUsers() {
        return registeredUsers;
    }

    public boolean registerUser(String username) {
        if (registeredUsers.size() < maxParticipants && !registeredUsers.contains(username)) {
            registeredUsers.add(username);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Event: " + title + "\nDescription: " + description +
               "\nDate/Time: " + dateTime + "\nLocation: " + location +
               "\nCategory: " + category + "\nMax Participants: " + maxParticipants +
               "\nCurrently Registered: " + registeredUsers.size();
    }
}
