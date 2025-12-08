package com.example.workio.data.model;

public class Payroll {
    private String id;
    private String employeeId;
    private double totalSalary;
    private String period;
    private String createdAt;

    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public double getTotalSalary() { return totalSalary; }
    public String getPeriod() { return period; }
    public String getCreatedAt() { return createdAt; }
}
