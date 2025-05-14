package com.example.schoolhub.Model;

import java.security.Timestamp;

public class ClassInfo {
    int id;
    String name;
    String room;
    String subject;

    public ClassInfo(int id, String name, String room, String subject) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}