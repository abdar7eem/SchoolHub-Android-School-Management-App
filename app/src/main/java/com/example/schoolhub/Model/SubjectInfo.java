package com.example.schoolhub.Model;

public class SubjectInfo {
    public int id;
    public String name;

    public SubjectInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
