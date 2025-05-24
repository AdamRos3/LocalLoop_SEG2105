package com.example.localloop.resources;

public class Admin extends UserAccount {
    //public Admin(String inputtedUsername, String inputtedPassword) {
    public Admin() {
        //super(inputtedUsername, inputtedPassword);
        super("admin", "XPI76SZUqyCjVxgnUjm0");
        typeOfAccount = "Admin";
        //username = "admin";
        //password = "XPI76SZUqyCjVxgnUjm0";
        welcomeSequence();
    }

    private void welcomeSequence() {
        System.out.println("Welcome... ");
        allCreatedUsers();
    }

    public void allCreatedUsers() {
        for (UserAccount account : this.getUserAccounts()) {
            System.out.println(account);
        }
    }
}