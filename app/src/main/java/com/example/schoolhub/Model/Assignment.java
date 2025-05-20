package com.example.schoolhub.Model;
public class Assignment {
    private String title;
    private String subjectName;
    private String teacherName;
    private String dueDate;
    private String status;

    private int id;

    private String  attachmentPath;

    public Assignment(int id, String title, String subject, String teacher, String due, String status, String attachment) {
        this.id = id;
        this.title = title;
        this.subjectName = subject;
        this.teacherName = teacher;
        this.dueDate = due;
        this.status = status;
        this.attachmentPath = attachment;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }
    public int getId() {
        return id;
    }

    public String getTitle() { return title; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
}
