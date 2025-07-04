package com.example.localloop.backend;

import java.util.ArrayList;

public class Participant extends UserAccount {

    String username;
    String password;
    String userID;

    public Participant(String username, String password, String userID) {
        super(username, password, userID);
        this.username = username;
        this.password = password;
        this.userID = userID;
    }

    public void requestJoinEvent() {

    }
    public String toString() {
        return "participant: "+username+", ID: "+userID;
    }
}
