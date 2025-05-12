package com.example.schoolhub.Model;

import java.security.Timestamp;

public class Classroom {
    private int id;
    private String academicStage; // Primary, Middle, etc.
    private int grade;
    private String section;
    private String className; // computed from others
    private String year;
    private Integer homeroomTeacherId;
    private Timestamp createdAt;
}
