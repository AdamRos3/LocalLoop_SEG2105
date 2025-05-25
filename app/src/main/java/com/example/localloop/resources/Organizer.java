package com.example.localloop.resources;

public class Organizer extends UserAccount {

    public Organizer() {

    }
    public Organizer(String username, String password) {
        super(username, password);
    }

    @Override
    public String welcome() {
        return "Welcome " + username + "! You are logged in as Organizer";
    }
}