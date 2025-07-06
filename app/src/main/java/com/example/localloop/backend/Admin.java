package com.example.localloop.backend;

import com.example.localloop.resources.exception.InvalidEventCategoryNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryNameException;

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
    public void deleteUser(DatabaseConnection dbConnection, UserAccount user) throws NoSuchEventCategoryNameException, InterruptedException {
        dbConnection.deleteUser(user);
    }
    public void createEventCategory(DatabaseConnection dbConnection, EventCategory category) throws InvalidEventCategoryNameException, InterruptedException {
        dbConnection.createEventCategory(category);
    }
    public void deleteEventCategory(DatabaseConnection dbConnection, EventCategory category) throws NoSuchEventCategoryNameException, InterruptedException {
        dbConnection.deleteEventCategory(category);
    }
    public String toString() {
        return "admin: "+username+", ID: "+userID;
    }

}