package com.example.workio.data.model;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("_id") // 👈 BẮT BUỘC phải có dòng này
    private String id;
    private String title;
    private String message;
    private String status;
    private String createdAt;
    private String updatedAt;

    public String getId() { return id; }   // đổi lại getId()
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public void setStatus(String status) { this.status = status; }
}
