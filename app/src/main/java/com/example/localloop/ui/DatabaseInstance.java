package com.example.localloop.ui;

import com.example.localloop.model.DatabaseConnection;

class DatabaseInstance {
    private static DatabaseConnection dbConnection;
    private DatabaseInstance() {
    }
    protected static void set(DatabaseConnection db) {
        dbConnection = db;
    }
    protected static DatabaseConnection get() {
        return dbConnection;
    }
}