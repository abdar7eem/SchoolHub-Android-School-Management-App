package com.example.schoolhub.Model;

public class LeaderboardEntry {
    public String studentName;
    public int obtainedMarks;
    public int totalMarks;

    public LeaderboardEntry(String studentName, int obtainedMarks, int totalMarks) {
        this.studentName = studentName;
        this.obtainedMarks = obtainedMarks;
        this.totalMarks = totalMarks;
    }
}
