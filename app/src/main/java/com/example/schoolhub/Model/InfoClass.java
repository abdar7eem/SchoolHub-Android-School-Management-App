package com.example.schoolhub.Model;

public class InfoClass {
    private int id;
    private String name;

    public InfoClass(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String toString() {
        return name; // This tells the spinner what to display
    }
}
