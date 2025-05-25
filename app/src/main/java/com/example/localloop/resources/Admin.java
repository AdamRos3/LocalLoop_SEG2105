package com.example.localloop.resources;

public class Admin extends UserAccount {
    public Admin() {

    }

    //public Admin(String inputtedUsername, String inputtedPassword) {
    public Admin(String username, String password) {
        super(username, password);
        //username = "admin";
        //password = "XPI76SZUqyCjVxgnUjm0";
        welcomeSequence();
    }

    private void welcomeSequence() {
        System.out.println("Welcome... ");
        allCreatedUsers();
    }

    public void allCreatedUsers() {
    }

    @Override
    public String welcome() {
        return "Welcome " + username + "! You are logged is as Admin";
    }
}