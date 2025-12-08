package com.example.workio.data.model;

public class RegisterShiftRequest {
    private String employeeId;
    private String shiftId;
    private String date;

    public RegisterShiftRequest(String employeeId, String shiftId, String date, String note) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.date = date;
    }

    public String getEmployeeId() { return employeeId; }
    public String getShiftId() { return shiftId; }
    public String getDate() { return date; }
}
