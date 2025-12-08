package com.example.workio.data.model;

public class VerifyEmailRequest {
    private String email;
    private String code;

    public VerifyEmailRequest(String email, String code) {
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
