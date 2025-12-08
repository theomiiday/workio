package com.example.workio.data.model;

import com.google.gson.internal.LinkedTreeMap;

public class ShiftRegistration {

    private String _id;
    private Object employeeId;
    private Object shiftId;
    private String date;
    private String status;
    private String note;

    // ========= ID =========
    public String getId() {
        return _id;
    }
    public void setId(String _id) {
        this._id = _id;
    }

    // ========= EMPLOYEE =========
    public String getEmployeeId() {
        if (employeeId == null) return null;

        if (employeeId instanceof String) {
            // Trường hợp server trả về ID dạng chuỗi
            return (String) employeeId;
        } else if (employeeId instanceof LinkedTreeMap) {
            // Trường hợp server trả object, bóc _id
            Object id = ((LinkedTreeMap<?, ?>) employeeId).get("_id");
            return id != null ? id.toString() : null;
        }
        return null;
    }

    public String getEmployeeName() {
        if (employeeId instanceof LinkedTreeMap) {
            Object name = ((LinkedTreeMap<?, ?>) employeeId).get("name");
            return name != null ? name.toString() : "";
        }
        return "";
    }

    public void setEmployeeId(Object employeeId) {
        this.employeeId = employeeId;
    }

    // ========= SHIFT =========
    public String getShiftId() {
        if (shiftId == null) return null;

        if (shiftId instanceof String) {
            return (String) shiftId;
        } else if (shiftId instanceof LinkedTreeMap) {
            Object id = ((LinkedTreeMap<?, ?>) shiftId).get("_id");
            return id != null ? id.toString() : null;
        }
        return null;
    }

    public String getShiftName() {
        if (shiftId instanceof String) return "(ID: " + shiftId + ")";
        if (shiftId instanceof Shift) return ((Shift) shiftId).getShiftName();
        if (shiftId instanceof LinkedTreeMap) {
            Object name = ((LinkedTreeMap<?, ?>) shiftId).get("shiftName");
            return name != null ? name.toString() : "";
        }
        return "";
    }


    public String getShiftTimeRange() {
        if (shiftId == null) return "";

        if (shiftId instanceof Shift) {
            Shift s = (Shift) shiftId;
            if (s.getStartTime() != null && s.getEndTime() != null) {
                return s.getStartTime() + " - " + s.getEndTime();
            }
        } else if (shiftId instanceof com.google.gson.internal.LinkedTreeMap) {
            Object start = ((com.google.gson.internal.LinkedTreeMap<?, ?>) shiftId).get("startTime");
            Object end = ((com.google.gson.internal.LinkedTreeMap<?, ?>) shiftId).get("endTime");
            return (start != null && end != null) ? start + " - " + end : "";
        }
        return "";
    }


    public void setShiftId(Object shiftId) {
        this.shiftId = shiftId;
    }

    // ========= DATE / STATUS / NOTE =========
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

}
