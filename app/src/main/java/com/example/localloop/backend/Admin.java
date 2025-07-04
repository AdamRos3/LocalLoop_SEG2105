package com.example.localloop.backend;

public class Admin extends UserAccount {

    String usernameString;
    String password;
    String userID;

    public Admin(String username, String password, String userID) {
        super(username, password, userID);
    }

    public void removeUser(String userID) {

    }

}