package com.example.localloop.model;

import java.util.ArrayList;

public class Participant extends UserAccount {

    private String username;
    private String password;
    private String userID;

    public Participant(){
        // Empty constructor is required by Firebase
    }

    public Participant(String username, String password, String userID) {
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
    public ArrayList<Event> getAllEvents(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllEvents();
    }
    public void requestJoinEvent(DatabaseConnection dbConnection, Event event) throws InterruptedException {
        dbConnection.requestJoinEvent(event);
    }
    public ArrayList<Event> getJoinRequests(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getJoinRequests();
    }
    public String toString() {
        return "participant: "+username+", ID: "+userID;
    }
}
