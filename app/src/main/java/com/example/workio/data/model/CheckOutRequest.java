package com.example.workio.data.model;

public class CheckOutRequest {
    private String attendanceId;
    private double latitude;
    private double longitude;

    public CheckOutRequest(String attendanceId, double latitude, double longitude) {
        this.attendanceId = attendanceId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getAttendanceId() { return attendanceId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
