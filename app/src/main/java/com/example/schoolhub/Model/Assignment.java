package com.example.schoolhub.Model;

public class Assignment {
    private int id;
    private String title;
    private String subjectName;
    private String teacherName;
    private String dueDate;
    private String status;
    private String attachmentPath;

    private int classId;
    private int teacherId;
    private int subjectId;

    public Assignment(int id, String title, String subject, String teacher, String due, String status, String attachment) {
        this.id = id;
        this.title = title;
        this.subjectName = subject;
        this.teacherName = teacher;
        this.dueDate = due;
        this.status = status;
        this.attachmentPath = attachment;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getAttachmentPath() { return attachmentPath; }

    public int getClassId() { return classId; }
    public int getTeacherId() { return teacherId; }
    public int getSubjectId() { return subjectId; }

    // Setters for additional IDs
    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
