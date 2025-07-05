package com.example.localloop.backend;

import java.util.Dictionary;

import java.util.ArrayList;

public class Event {
    private String title;
    private String description;
    private String dateTime;
    private String location;
    //private static ArrayList<String> categoryList;
    //private static ArrayList<Dictionary> categoryList;
    private static Dictionary<String, String> categoryList;
    //private String category;
    private int maxParticipants;
    private ArrayList<String> registeredUsers;

    public Event(String title, String description, String dateTime, String location,
                 String category, String categoryDescription, int maxParticipants) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        //this.category = category;
        addNewEventCategory(category, categoryDescription);
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

    //public String getCategory() {
    //    return category;
    //}

    public void addNewEventCategory(String cat, String catDescription) {
        categoryList.put(cat, catDescription);
    }

    public void editCategories(String eddittedcat, String cat, String categories) {
        categoryList.remove(eddittedcat);
        addNewEventCategory(cat, categories);
        /* 
        if (categoryList != null && categoryList.get(cat)) {
            editCategorieRemoveThenAdd(cat, categories);
        }
        categoryList.add() */
    }

    public void deleteCategories(String catDelete) {
        categoryList.remove(catDelete);
    }
    /* 
    public void editCategorieRemoveThenAdd(String cat, String categories) {
        categoryList.remove(cat);
        categoryList.put(cat, categories)
    } */

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
               "\nCategory: " + categoryList + "\nMax Participants: " + maxParticipants +
               "\nCurrently Registered: " + registeredUsers.size();
    }
}
