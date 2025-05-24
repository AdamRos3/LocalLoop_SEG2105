package com.example.localloop.resources;

public class Participant extends UserAccount {
    public Participant(String inputtedUsername, String inputtedPassword) {
        super(inputtedUsername, inputtedPassword);
        typeOfAccount = "Participant";
        /* comment test */
    }
}
