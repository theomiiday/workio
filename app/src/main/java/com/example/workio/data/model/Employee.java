package com.example.workio.data.model;

public class Employee {
    private String _id;
    private String name;
    private String username;
    private String email;
    private String role;
    private String phone;
    private String branchId;
    private boolean isEmailVerified;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public Employee() {}

    // Full constructor for creating new employee
    public Employee(String name, String username, String email, String password,
                    String role, String phone, String branchId) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.branchId = branchId;
    }

    // Getters and Setters
    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public boolean isEmailVerified() { return isEmailVerified; }
    public void setEmailVerified(boolean emailVerified) { isEmailVerified = emailVerified; }

    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}