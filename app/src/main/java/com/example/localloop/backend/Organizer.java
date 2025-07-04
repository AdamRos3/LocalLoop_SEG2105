package com.example.localloop.backend;

public class Organizer extends UserAccount {

    private String username;
    private String password;
    private String userID;

    public Organizer() {
        // Empty constructor is required by Firebase
    }
    public Organizer(String username, String password, String userID) {
        super(username, password, userID);
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
    public void editEvents() {
        System.out.println("events edited!!!");
    }
    public String toString() {
        return "organizer: "+username+", ID: "+userID;
    }
}