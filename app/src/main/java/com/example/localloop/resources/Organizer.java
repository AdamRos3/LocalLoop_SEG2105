package com.example.localloop.resources;

public class Organizer extends UserAccount {
    public Organizer(String inputtedUsername, String inputtedPassword) {
        super(inputtedUsername, inputtedPassword);
        typeOfAccount = "Organizer";
    }
}