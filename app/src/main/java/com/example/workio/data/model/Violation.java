package com.example.workio.data.model;

public class Violation {
    private String _id;
    private Employee employeeId;
    private Branch branchId;
    private String title;
    private String description;
    private String violationDate;
    private int penaltyAmount;
    private String status;
    private CreatedBy createdBy;

    public String getId() { return _id; }
    public Employee getEmployeeId() { return employeeId; }
    public Branch getBranchId() { return branchId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getViolationDate() { return violationDate; }
    public int getPenaltyAmount() { return penaltyAmount; }
    public String getStatus() { return status; }
    public CreatedBy getCreatedBy() { return createdBy; }

    public static class Employee {
        private String _id;
        private String name;
        private String email;
        private String role;

        public String getId() { return _id; }
        public String getName() { return name; }
    }

    public static class Branch {
        private String _id;
        private String branchName;

        public String getBranchName() { return branchName; }
    }

    public static class CreatedBy {
        private String _id;
        private String name;
        private String role;

        public String getName() { return name; }
    }
}

