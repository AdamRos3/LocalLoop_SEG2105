package com.example.localloop.backend;

public class Organizer extends UserAccount {

    String username;
    String password;
    String userID;

    public Organizer(String username, String password, String userID) {
        super(username, password, userID);
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