package com.example.workio.data.model;

public class ResendVerificationRequest {
    private String email;

    public ResendVerificationRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
