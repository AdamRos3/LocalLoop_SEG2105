package com.example.localloop.resources;

import java.util.ArrayList;

public abstract class UserAccount {
    public String username;
    public String password;

    public UserAccount() {

    }

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public abstract void welcome();
}