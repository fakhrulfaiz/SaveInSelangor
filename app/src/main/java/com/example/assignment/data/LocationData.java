package com.example.assignment.data;

public class LocationData {
    public double lat;
    public double lng;

    public LocationData() {
        // Default constructor required for Firebase
    }

    public LocationData(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
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
}

