package com.example.workio.data.model;

public class CheckInRequest {
    private String registrationId;
    private double latitude;
    private double longitude;

    public CheckInRequest(String registrationId,double latitude, double longitude) {
        this.registrationId = registrationId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getRegistrationId() { return registrationId;}
}