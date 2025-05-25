package com.example.localloop.resources;

public class TestParticipant {
    public static void main(String[] args) {
        Participant p = new Participant("test", "123");
        Events e = new Events("Yoga", "Relaxing", "2025-06-01", "Studio A", "Wellness", 20);
        p.registerForEvent(e);
    }
}