package com.example.schoolhub.Model;
public class Assignment {
    private String title;
    private String subjectName;
    private String teacherName;
    private String dueDate;
    private String status;

    public Assignment(String title, String subjectName, String teacherName, String dueDate, String status) {
        this.title = title;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
}
