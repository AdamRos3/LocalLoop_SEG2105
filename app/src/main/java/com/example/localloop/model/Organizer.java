package com.example.localloop.model;

import com.example.localloop.resources.datetime.Date;
import com.example.localloop.resources.datetime.Time;
import com.example.localloop.resources.exception.InvalidEventNameException;
import com.example.localloop.resources.exception.NoSuchEventCategoryException;
import com.example.localloop.resources.exception.NoSuchEventException;

import java.util.ArrayList;

public class Organizer extends UserAccount {

    private String username;
    private String password;
    private String userID;

    public Organizer() {
        // Empty constructor is required by Firebase
    }
    public Organizer(String username, String password, String userID) {
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
    protected void setUserID(String str) {
        this.userID = str;
    }
    public void createEvent(DatabaseConnection dbConnection, Event event) throws NoSuchEventCategoryException, InvalidEventNameException, InterruptedException {
        dbConnection.createEvent(event);
    }
    public void editEvent(DatabaseConnection dbConnection,Event eventToEdit, Event editedEvent) throws NoSuchEventException, InvalidEventNameException, InterruptedException, NoSuchEventCategoryException {
        dbConnection.editEvent(eventToEdit,editedEvent);
    }
    public void deleteEvent(DatabaseConnection dbConnection, Event event) throws NoSuchEventException, InterruptedException {
        dbConnection.deleteEvent(event);
    }
    public ArrayList<Event> getUserEvents(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getUserEvents();
    }
    public ArrayList<EventCategory> getAllEventCategories(DatabaseConnection dbConnection) throws InterruptedException {
        return dbConnection.getAllEventCategories();
    }
    public String toString() {
        return "organizer: "+username+", ID: "+userID;
    }
}