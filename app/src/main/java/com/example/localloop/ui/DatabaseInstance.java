package com.example.localloop.ui;

import com.example.localloop.backend.Admin;
import com.example.localloop.backend.DatabaseConnection;
import com.example.localloop.backend.UserAccount;

class DatabaseInstance {
    private DatabaseInstance() {
    }
    public static DatabaseConnection get() {
        return Login.dbConnection;
    }
}