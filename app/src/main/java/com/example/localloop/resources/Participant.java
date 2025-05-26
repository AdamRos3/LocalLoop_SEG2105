package com.example.localloop.resources;

import java.util.ArrayList;

public class Participant extends UserAccount {
    private final ArrayList<Events> registeredEvents; // To store events that the participant registred for

    public Participant() {
        registeredEvents = new ArrayList<>();
    }

    public Participant(String username, String password, String userID) {
        super(username, password, userID);
        registeredEvents = new ArrayList<>();
    }

    //Register for an event
    public boolean registerForEvent(Events event){
        if(event.registerUser(getUsername())){
            registeredEvents.add(event);
            return true;
        }
        return false;
    }

    //Get list of events the user has registered for
    public ArrayList<Events> getRegisteredEvents(){
        return registeredEvents;
    }

    @Override
    public String welcome() {
        return "Welcome " + username + "! You are logged in as Participant";
    }
}
