package com.example.schoolhub.Model;

public class AssignmentInfo {
    public int id;
    public String title;

    public AssignmentInfo(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
