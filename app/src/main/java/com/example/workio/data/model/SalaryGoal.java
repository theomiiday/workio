package com.example.workio.data.model;

public class SalaryGoal {
    private String id;
    private EmployeeDetails employeeId;
    private int targetShifts;
    private int currentShifts;
    private double currentEarnings;
    private int month; // 1-12
    private int year;
    private String status; // "active", "completed", "failed"
    private String createdAt;
    private String updatedAt;

    // Constructors
    public SalaryGoal() {}

    public SalaryGoal(int targetShifts, int month, int year) {
        this.targetShifts = targetShifts;
        this.month = month;
        this.year = year;
        this.currentShifts = 0;
        this.currentEarnings = 0;
        this.status = "active";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public EmployeeDetails getEmployeeId() { return employeeId; }
    public void setEmployeeId(EmployeeDetails employeeId) { this.employeeId = employeeId; }

    public int getTargetShifts() { return targetShifts; }
    public void setTargetShifts(int targetShifts) { this.targetShifts = targetShifts; }

    public int getCurrentShifts() { return currentShifts; }
    public void setCurrentShifts(int currentShifts) { this.currentShifts = currentShifts; }

    public double getCurrentEarnings() { return currentEarnings; }
    public void setCurrentEarnings(double currentEarnings) { this.currentEarnings = currentEarnings; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public String getMonthYearString() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month - 1] + " " + year;
    }

    public int getProgressPercentage() {
        if (targetShifts == 0) return 0;
        return Math.min(100, (currentShifts * 100) / targetShifts);
    }

    public boolean isAchieved() {
        return currentShifts >= targetShifts;
    }

    public int getRemainingShifts() {
        return Math.max(0, targetShifts - currentShifts);
    }

    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isFailed() {
        return "failed".equals(status);
    }
}