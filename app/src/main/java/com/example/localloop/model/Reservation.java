package com.example.localloop.model;

public class Reservation {
    private String attendeeID;
    private String eventID;
    private String reservationID;
    public Reservation() {
        // Empty constructor is required by Firebase
    }
    public Reservation(String attendeeID, String eventID, String reservationID) {
        this.attendeeID = attendeeID;
        this.eventID = eventID;
        this.reservationID = reservationID;
    }
    public String getAttendeeID() {
        return attendeeID;
    }
    public String getEventID() {
        return eventID;
    }
    public String getReservationID() {
        return reservationID;
    }
    public String toString() {
        return attendeeID + " is attending " + eventID;
    }
}
