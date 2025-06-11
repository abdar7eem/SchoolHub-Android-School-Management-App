package com.example.schoolhub.Model;

public class TeacherInfo {
    private int id;
    private String name;

    public TeacherInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}

