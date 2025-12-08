package com.example.workio.data.model;

public class VerifyResetOtpRequest {
    private String email;
    private String code;
    public VerifyResetOtpRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }
    public String getEmail() {
        return email;
    }
    public String getCode() {
        return code;
    }
}
