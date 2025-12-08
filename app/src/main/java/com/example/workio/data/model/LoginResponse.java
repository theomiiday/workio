package com.example.workio.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String message;
    private Data data;

    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("accessToken")
        private String accessToken;

        @SerializedName("user")
        private User user;

        public String getAccessToken() { return accessToken; }
        public User getUser() { return user; }
    }

    public static class User {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        @SerializedName("role")
        private String role;

        @SerializedName("branchId")
        private String branchId;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public String getBranchId() { return branchId; }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }
    }
}