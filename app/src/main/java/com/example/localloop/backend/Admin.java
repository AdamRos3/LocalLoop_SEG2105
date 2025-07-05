package com.example.localloop.backend;

public class Admin extends UserAccount {

    String username;
    String password;
    String userID;

    public Admin() {
        // Empty constructor is required by Firebase
    }
    public Admin(String username, String password, String userID) {
        super(username, password, userID);
        this.username = username;
        this.password = password;
        this.userID = userID;
    }

    public void removeUser(String userID) {

    }

}