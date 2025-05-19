package com.example.schoolhub.Model;

public class Assignment {

    private int id;
    private String title;
    private String subject;
    private String teacher;
    private String dueDate;
    private String status;
    private String attachment; // Relative file path (e.g., uploads/file123.pdf)

    public Assignment(int id, String title, String subject, String teacher, String dueDate, String status, String attachment) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.teacher = teacher;
        this.dueDate = dueDate;
        this.status = status;
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
