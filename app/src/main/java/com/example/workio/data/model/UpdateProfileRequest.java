package com.example.workio.data.model;

public class UpdateProfileRequest {
    private String name;
    private String phone;
    private String email;

    public UpdateProfileRequest(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}

