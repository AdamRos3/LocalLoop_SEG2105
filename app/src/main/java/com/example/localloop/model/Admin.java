package com.example.localloop.model;

import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.example.localloop.resources.exception.NoSuchUserException;

import java.util.ArrayList;

public class Admin extends UserAccount {

    private String username;
    private String password;
    private String userID;

    public Admin() {
        // Empty constructor is required by Firebase
    }
    public Admin(String username, String password, String userID) {
        super(username, password, userID);
        this.username = username;
        this.password = password;
        this.userID = userID;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getUserID() {
        return userID;
    }
    public ArrayList<Participant> getAllParticipants(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllParticipants();
    }
    public ArrayList<Organizer> getAllOrganizers(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllOrganizers();
    }
    public void deleteUser(DatabaseConnection dbConnection, UserAccount user) throws NoSuchUserException, NoSuchEventException, InterruptedException {
        dbConnection.deleteUser(user);
    }
    public void deleteUser(DatabaseConnection dbConnection, String userID) throws NoSuchUserException, NoSuchEventException, InterruptedException {
        dbConnection.deleteUser(userID);
    }
    public void createEventCategory(DatabaseConnection dbConnection, EventCategory category) throws InvalidEventCategoryNameException, InterruptedException {
        dbConnection.createEventCategory(category);
    }
    public void deleteEventCategory(DatabaseConnection dbConnection, EventCategory category) throws NoSuchEventCategoryException, NoSuchEventException, InterruptedException {
        dbConnection.deleteEventCategory(category);
    }
    public void editEventCategory(DatabaseConnection dbConnection, EventCategory category, String name, String description) throws InvalidEventNameException, NoSuchEventCategoryException, InterruptedException {
        dbConnection.editEventCategory(category,name,description);
    }
    public ArrayList<EventCategory> getAllEventCategories(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllEventCategories();
    }
    public ArrayList<Event> getAllEvents(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllEvents();
    }
    public String toString() {
        return "admin: "+username+", ID: "+userID;
    }

}