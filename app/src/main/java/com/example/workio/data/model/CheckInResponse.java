package com.example.workio.data.model;

public class CheckInResponse {
    private String _id;                 // ID của Attendance record
    private String date;                // Ngày làm việc
    private String checkInTime;         // Giờ check-in (ISO)
    private String checkOutTime;        // Giờ check-out (nếu có)
    private String status;              // "checked-in", "checked-out"
    private Shift shiftId;              // Thông tin ca làm
    private Employee employeeId;        // Nhân viên
    private ShiftRegistration registrationId; // Đăng ký ca
    private Location checkInLocation;   // GPS
    private Location checkOutLocation;  // GPS

    // --- Inner class để map JSON GPS location ---
    public static class Location {
        private double latitude;
        private double longitude;

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }

    // --- Getter ---
    public String get_id() { return _id; }
    public String getDate() { return date; }
    public String getCheckInTime() { return checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public String getStatus() { return status; }
    public Shift getShiftId() { return shiftId; }
    public Employee getEmployeeId() { return employeeId; }
    public ShiftRegistration getRegistrationId() { return registrationId; }
    public Location getCheckInLocation() { return checkInLocation; }
    public Location getCheckOutLocation() { return checkOutLocation; }
}
