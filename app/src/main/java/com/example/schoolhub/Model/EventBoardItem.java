package com.example.schoolhub.Model;

public class EventBoardItem {
    public int id;
    public String title, dateTime, location, type, description;

    public EventBoardItem(int id, String title, String dateTime, String location, String type, String description) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.type = type;
        this.description = description;
    }
}
