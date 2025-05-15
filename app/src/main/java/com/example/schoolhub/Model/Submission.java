package com.example.schoolhub.Model;

public class Submission {
    private final int id;
    private final String studentName;
    private final String subjectName;
    private final String submissionDate;
    private final String fileUrl;

    public Submission(int id, String studentName, String subjectName, String submissionDate, String fileUrl) {
        this.id = id;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.submissionDate = submissionDate;
        this.fileUrl = fileUrl;
    }

    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
