package com.example.schoolhub.Model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Schedule {

    private int id;
    private int classId;
    private int subjectId;
    private String dayOfWeek;  // e.g., "Monday", "Tuesday"
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String subjectName;       // Show subject
    private String instructorName;    // Show teacher

    public Schedule() {
        // Default constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Schedule(int id, int classId, int subjectId, String dayOfWeek,
                    String startTime, String endTime, String room,
                    String subjectName, String instructorName) {
        this.id = id;
        this.classId = classId;
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;

        // Expecting format like "8:30 AM", "2:15 PM"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        this.startTime = LocalTime.parse(startTime, formatter);
        this.endTime = LocalTime.parse(endTime, formatter);

        this.room = room;
        this.subjectName = subjectName;
        this.instructorName = instructorName;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}
