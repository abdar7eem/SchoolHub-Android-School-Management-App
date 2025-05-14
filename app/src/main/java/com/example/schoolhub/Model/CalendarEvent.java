package com.example.schoolhub.Model;

public class CalendarEvent {
    public int id;
    public String type, subject, title, time, location;

    public CalendarEvent(int id, String type, String subject, String title, String time, String location) {
        this.id = id;
        this.type = type;
        this.subject = subject;
        this.title = title;
        this.time = time;
        this.location = location;
    }
}
