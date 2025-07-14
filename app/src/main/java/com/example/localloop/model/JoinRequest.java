package com.example.localloop.model;

public class JoinRequest {
    private String participantID;
    private String eventID;
    private String joinRequestID;
    public JoinRequest() {
        // Empty constructor is required by Firebase
    }
    public JoinRequest(String participantID, String eventID, String joinRequestID) {
        this.participantID = participantID;
        this.eventID = eventID;
        this.joinRequestID = joinRequestID;
    }
    public String getParticipantID() {
        return participantID;
    }
    public String getEventID() {
        return eventID;
    }
    public String getJoinRequestID() {
        return joinRequestID;
    }
    public String toString() {
        return participantID + " requests to join " + eventID;
    }
}
