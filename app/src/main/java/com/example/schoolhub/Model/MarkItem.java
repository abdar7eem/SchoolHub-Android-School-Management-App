package com.example.schoolhub.Model;

public class MarkItem {
    private String subjectName;
    private String details;
    private String grade;

    public MarkItem(String subjectName, String details, String grade) {
        this.subjectName = subjectName;
        this.details = details;
        this.grade = grade;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDetails() {
        return details;
    }

    public String getGrade() {
        return grade;
    }
}

