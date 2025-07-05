package com.example.localloop.backend;

public class EventCategory {
    private String name;
    private String description;
    private String categoryID;
    public EventCategory() {
        // Empty constructor is required by Firebase
    }
    public EventCategory(String name, String description, String categoryID) {
        this.name = name;
        this.description = description;
        this.categoryID = categoryID;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getCategoryID() {
        return categoryID;
    }
}
