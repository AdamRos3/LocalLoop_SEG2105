package com.example.localloop.backend;

public abstract class UserAccount {

    private String username;
    private String password;
    private String userID;

    UserAccount() {
        // Empty constructor is required by Firebase
    }
    UserAccount(String username, String password, String userID) {
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
    public String toString() {
        return "user: "+username+", ID: "+userID;
    }
}