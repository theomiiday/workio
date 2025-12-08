package com.example.workio.data.model;

public class ResetPasswordRequest {
    private String email;
    private String code;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequest(String email, String code, String newPassword, String confirmPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
}
