package com.example.schoolhub.Model;

public class Submission {
    String studentName, subjectName, submissionDate, fileUrl;
    int submissionId;

    public Submission(int submissionId, String studentName, String subjectName,
                      String submissionDate, String fileUrl) {
        this.submissionId = submissionId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.submissionDate = submissionDate;
        this.fileUrl = fileUrl;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }
}
