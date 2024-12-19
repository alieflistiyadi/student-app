package com.example.studentapp.models;

public class Subject {
    private String id;
    private String name;
    private int credits;
    private String category;

    // Required for Firestore
    public Subject() {}

    public Subject(String id, String name, int credits, String category) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
