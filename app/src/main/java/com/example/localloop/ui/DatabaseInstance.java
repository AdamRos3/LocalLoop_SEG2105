package com.example.localloop.ui;

import com.example.localloop.backend.DatabaseConnection;

class DatabaseInstance {
    private DatabaseInstance(){}
    public static DatabaseConnection get() {
        return Login.dbConnection;
    }
}
