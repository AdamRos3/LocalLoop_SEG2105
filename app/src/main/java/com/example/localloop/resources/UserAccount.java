package com.example.localloop.resources;

import java.util.ArrayList;

public abstract class UserAccount {
    public String username;
    public String password;
    public String userID;

    public UserAccount() {

    }

    public UserAccount(String username, String password, String userID) {
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

    public String getUserID() { return userID; }
    public abstract String welcome();
}