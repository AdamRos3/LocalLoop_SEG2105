package com.example.localloop.model;

import com.example.localloop.resources.exception.InvalidJoinRequestException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;
import com.example.localloop.resources.exception.NoSuchRequestException;
import com.example.localloop.resources.exception.NoSuchReservationException;

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
    public ArrayList<EventCategory> getAllEventCategories(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllEventCategories();
    }
    public void requestJoinEvent(DatabaseConnection dbConnection, Event event) throws InvalidJoinRequestException, NoSuchEventException, InterruptedException {
        dbConnection.requestJoinEvent(event);
    }
    public ArrayList<Event> getJoinRequests(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getJoinRequests();
    }

    public void cancelJoinRequest(DatabaseConnection dbConnection, Event event) throws NoSuchRequestException, InterruptedException {
        dbConnection.cancelJoinRequest(event);
    }
    public ArrayList<Event> getReservations(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getReservations();
    }
    public void cancelReservation(DatabaseConnection dbConnection, Event event) throws NoSuchReservationException, InterruptedException {
        dbConnection.cancelReservation(event);
    }
    public Event eventSearch(DatabaseConnection dbConnection, String name) throws NoSuchEventException, InterruptedException {
        return dbConnection.eventSearch(name);
    }
    public ArrayList<Event> eventSearch(DatabaseConnection dbConnection, EventCategory category) throws NoSuchEventCategoryException, InterruptedException {
        return dbConnection.eventSearch(category);
    }
    public String toString() {
        return "participant: "+username+", ID: "+userID;
    }
}
