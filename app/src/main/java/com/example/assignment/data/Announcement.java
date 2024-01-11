package com.example.assignment.data;

import java.util.Date;

public class Announcement {
    private String subject;
    private String description;
    private String date;

    // Constructor
    public Announcement() {
        // Default constructor
    }

    public Announcement(String subject, String description, String date) {
        this.subject = subject;
        this.description = description;
        this.date = date;
    }

    // Getter and Setter methods for Subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and Setter methods for Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and Setter methods for Date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
