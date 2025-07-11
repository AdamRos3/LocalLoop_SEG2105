package com.example.localloop.model;

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
    public void requestJoinEvent() {

    }
    public String toString() {
        return "participant: "+username+", ID: "+userID;
    }
}
