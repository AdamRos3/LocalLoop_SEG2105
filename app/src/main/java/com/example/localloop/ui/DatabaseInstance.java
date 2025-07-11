package com.example.localloop.ui;

import com.example.localloop.model.DatabaseConnection;

class DatabaseInstance {
    private DatabaseInstance() {
    }
    protected static DatabaseConnection get() {
        return Login.dbConnection;
    }
}