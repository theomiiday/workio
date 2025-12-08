package com.example.workio.data.model;

public class UpdateEmailRequest {
    private String email;

    public UpdateEmailRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
