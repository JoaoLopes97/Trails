package com.example.trails.model;

import java.io.Serializable;

public class Coordinates implements Serializable {

    private double latitude;
    private double longitude;


    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return "Longitude: " + getLongitude() + "- Latitude: " + getLatitude();
    }
}
