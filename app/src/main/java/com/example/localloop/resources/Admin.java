package com.example.localloop.resources;

public class Admin extends UserAccount {
    public Admin() {

    }

    //public Admin(String inputtedUsername, String inputtedPassword) {
    public Admin(String username, String password, String userID) {
        super(username, password, userID);
        //username = "admin";
        //password = "XPI76SZUqyCjVxgnUjm0";
        //userID = "-OR8hNVtR8ECnVq9jcAZ";
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