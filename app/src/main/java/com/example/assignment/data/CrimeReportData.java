package com.example.assignment.data;

public class CrimeReportData {

    private double lat;
    private double lng;
    private String type;
    private String date;
    private String time;
    private String description;

    public CrimeReportData() {
        // Default constructor required for Firebase
    }

    public CrimeReportData(double lat, double lng, String type, String date, String time, String description) {
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

