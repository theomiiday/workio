package com.example.workio.data.model;

import java.util.Map;

public class Shift {
    private String _id;
    private String shiftName;
    private String startTime;
    private String endTime;
    private Object branchId; // có thể là String hoặc Object

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // ✅ Không return String trực tiếp nữa, vì branchId là Object
    public Object getBranchId() {
        return branchId;
    }

    public void setBranchId(Object branchId) {
        this.branchId = branchId;
    }

    // ✅ Hàm hỗ trợ ép kiểu sang String khi cần dùng
    public String getBranchIdString() {
        if (branchId instanceof String) {
            return (String) branchId;
        } else if (branchId instanceof Map) {
            Object id = ((Map<?, ?>) branchId).get("_id");
            return id != null ? id.toString() : null;
        }
        return null;
    }
}
