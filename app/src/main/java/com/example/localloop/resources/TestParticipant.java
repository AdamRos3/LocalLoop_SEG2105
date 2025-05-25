package com.example.localloop.resources;

public class TestParticipant {
    public static void main(String[] args) {
        Participant p = new Participant("username", "password");
        System.out.println("Created participant: " + p.getUsername());
    }
}