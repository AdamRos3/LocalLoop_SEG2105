package com.example.localloop.resources;

import java.util.ArrayList;

public class Participant extends UserAccount {
    private ArrayList<Events> registeredEvents; // To store events that the participant registred for
    public Participant(String inputtedUsername, String inputtedPassword) {
        super(inputtedUsername, inputtedPassword);
        typeOfAccount = "Participant";
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

}
