package com.example.workio.ui.main.shift;

// Thay đổi bằng tên package của bạn

public class ShiftRequest {
    private String shiftName;
    private String status;
    private String checkInTime;
    private String checkOutTime;
    // Định nghĩa các hằng số trạng thái
    // Bỏ trạng thái CHỜ DUYỆT ĐK khỏi UI, thay bằng ẨN
    public static final String STATUS_PENDING_REG = "CHỜ DUYỆT ĐK"; // Đã đăng ký nhưng chưa được duyệt
    public static final String STATUS_APPROVED = "ĐÃ ĐƯỢC DUYỆT";
    public static final String STATUS_PENDING_CANCEL = "CHỜ DUYỆT HỦY";
    public static final String STATUS_CANCELLED_APP = "ĐÃ HỦY DUYỆT";

    public ShiftRequest(String shiftName, String status) {
        this.shiftName = shiftName;
        this.status = status;
    }

    public String getShiftName() {
        return shiftName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShiftRequest that = (ShiftRequest) o;
        return shiftName.equals(that.shiftName);
    }

    @Override
    public int hashCode() {
        return shiftName.hashCode();
    }
    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
}
