package com.example.workio.data.model;

import com.google.gson.annotations.SerializedName;

public class Branch {
    private String _id;

    private String branchName;

    private String address;
    private Location location;
    private String createdAt;

    public static class Location {
        private double latitude;
        private double longitude;
        private int radius;

        // Getters
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public int getRadius() { return radius; }
    }

    // Getters
    public String getId() { return _id; }
    public String getBranchName() { return branchName; }
    public String getAddress() { return address; }
    public Location getLocation() { return location; }
    public String getCreatedAt() { return createdAt; }
}