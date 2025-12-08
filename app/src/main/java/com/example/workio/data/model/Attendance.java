package com.example.workio.data.model;

public class Attendance {
    private String id;
    private Employee employeeId;       // BE: ref Employee
    private Shift shiftId;             // BE: ref Shift
    private Registration registrationId; // BE: ref ShiftRegistration
    private String date;               // ISO string
    private String checkInTime;        // ISO string
    private String checkOutTime;       // ISO string
    private Location checkInLocation;  // object { latitude, longitude }
    private Location checkOutLocation; // object { latitude, longitude }
    private String status;             // "checked-in", "checked-out", "absent"
    private String notes;              // optional
    private Double workHours;          // nullable
    private String createdAt;
    private String updatedAt;

    // ✅ Location inner class
    public static class Location {
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    // ✅ Inner object: Employee
    public static class Employee {
        private String _id;
        private String name;
        private String role;
        private String email;

        public String getId() {
            return _id;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public String getEmail() {
            return email;
        }
    }

    // ✅ Inner object: Shift
    public static class Shift {
        private String _id;
        private String shiftName;
        private String startTime;
        private String endTime;

        public String getId() {
            return _id;
        }

        public String getShiftName() {
            return shiftName;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    // ✅ Inner object: Registration
    public static class Registration {
        private String _id;
        private String employeeId;
        private String shiftId;
        private String date;
        private String status;
        private String note;

        public String getId() {
            return _id;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getShiftId() {
            return shiftId;
        }

        public String getDate() {
            return date;
        }

        public String getStatus() {
            return status;
        }

        public String getNote() {
            return note;
        }
    }

    // ✅ Getters cho Attendance chính
    public String getId() {
        return id;
    }

    public Employee getEmployeeId() {
        return employeeId;
    }

    public Shift getShiftId() {
        return shiftId;
    }

    public Registration getRegistrationId() {
        return registrationId;
    }

    public String getDate() {
        return date;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public Location getCheckInLocation() {
        return checkInLocation;
    }

    public Location getCheckOutLocation() {
        return checkOutLocation;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public Double getWorkHours() {
        return workHours;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
