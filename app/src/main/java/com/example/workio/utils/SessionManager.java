package com.example.workio.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.example.workio.data.model.LoginResponse;

public class SessionManager {
    private static final String PREF_NAME = "EMS";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USER = "user";
    private static final String KEY_NAME = "name";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private static final String KEY_CHECKIN = "check_in_time";
    private static final String KEY_CHECKOUT = "check_out_time";
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    /**
     * Save login session after successful login
     */
    public void saveSession(String accessToken, String refreshToken, LoginResponse.User user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accessToken", accessToken);
        editor.putString("refreshToken", refreshToken);
        editor.putString("employeeId", user.getId()); // ✅ Lưu employeeId
        editor.putString("role", user.getRole());
        editor.putString("name", user.getName()); // <-- Tên được lưu với key "name"
        if (user.getBranchId() != null)
            editor.putString("branchId", user.getBranchId());
        editor.apply();
    }

    /**
     * Get access token
     */
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Get refresh token
     */
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Get current user
     */
    public LoginResponse.User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, LoginResponse.User.class);
        }
        return null;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    /**
     * Check if user has specific role
     */
    public boolean hasRole(String role) {
        LoginResponse.User user = getUser();
        return user != null && user.getRole().equals(role);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return hasRole("admin");
    }

    /**
     * Check if user is manager
     */
    public boolean isManager() {
        return hasRole("manager");
    }

    /**
     * Clear session (logout)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update access token (after refresh)
     */
    public void updateAccessToken(String token) {
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }
    public void updateBranchId(String branchId) {
        editor.putString("branchId", branchId);
        editor.apply();
    }
    public String getBranchId() {
        return prefs.getString("branchId", null);
    }
    public void saveCheckInTime(String time) {
        editor.putString(KEY_CHECKIN, time);
        editor.apply();
    }

    public void saveCheckOutTime(String time) {
        editor.putString(KEY_CHECKOUT, time);
        editor.apply();
    }

    public String getCheckInTime() {
        return prefs.getString(KEY_CHECKIN, "");
    }

    public String getCheckOutTime() {
        return prefs.getString(KEY_CHECKOUT, "");
    }
    public boolean isRememberLogin() {
        return prefs.getBoolean("rememberLogin", false);
    }

    public void setRememberLogin(boolean remember) {
        editor.putBoolean("rememberLogin", remember);
        editor.apply();
    }
    public SharedPreferences getPrefs() {
        return prefs;
    }
    public String getEmployeeName() {
        return prefs.getString(KEY_NAME, null);
    }
    public String getEmployeeId() {
        return prefs.getString("employeeId", null);
    }
    public void saveAttendanceId(String id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("attendance_id", id);
        editor.apply();
    }

    public String getAttendanceId() {
        return prefs.getString("attendance_id", null);
    }

    public void saveTodayRegistrationId(String id) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("registration_id", id);
        editor.apply();
    }

    public String getTodayRegistrationId() {
        return prefs.getString("registration_id", null);
    }

    public void saveShiftTimes(long start, long end) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("shift_start_time", start);
        editor.putLong("shift_end_time", end);
        editor.apply();
    }

    public Long getShiftStartTime() {
        if (prefs.contains("shift_start_time")) {
            return prefs.getLong("shift_start_time", 0);
        }
        return null;
    }

    public Long getShiftEndTime() {
        if (prefs.contains("shift_end_time")) {
            return prefs.getLong("shift_end_time", 0);
        }
        return null;
    }
    public void setCheckedIn(boolean value) {
        editor.putBoolean("isCheckedIn", value).apply();
    }

    public boolean isCheckedIn() {
        return prefs.getBoolean("isCheckedIn", false);
    }


}