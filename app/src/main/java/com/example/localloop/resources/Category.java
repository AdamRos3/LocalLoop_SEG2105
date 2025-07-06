package com.example.localloop.resources;

public class Category {
    public String categoryID;
    public String name;
    public String description;

    public Category() {}

    public Category(String categoryID, String name, String description) {
        this.categoryID = categoryID;
        this.name = name;
        this.description = description;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
