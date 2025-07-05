package com.example.localloop.backend;

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
    public void removeUser(String userID) {

    }
    public String toString() {
        return "admin: "+username+", ID: "+userID;
    }

}