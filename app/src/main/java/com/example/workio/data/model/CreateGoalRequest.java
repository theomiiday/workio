package com.example.workio.data.model;

public class CreateGoalRequest {
    private int targetShifts;
    private Integer month; // Optional, defaults to current month
    private Integer year;  // Optional, defaults to current year

    // Constructor
    public CreateGoalRequest(int targetShifts) {
        this.targetShifts = targetShifts;
    }

    public CreateGoalRequest(int targetShifts, int month, int year) {
        this.targetShifts = targetShifts;
        this.month = month;
        this.year = year;
    }

    // Getters and Setters
    public int getTargetShifts() { return targetShifts; }
    public void setTargetShifts(int targetShifts) { this.targetShifts = targetShifts; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
