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
    private String startTime;
    private String endTime;
    private String room;
    private String subjectName;       // Show subject
    private String instructorName;    // Show teacher
    private String className;         // Show class

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

        this.startTime =startTime;
        this.endTime = endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
