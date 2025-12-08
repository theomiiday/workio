package com.example.workio.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Message {

    @SerializedName("_id")
    private String id;

    private String branchId;

    private LoginResponse.User senderId;
    private LoginResponse.User receiverId;

    private String messageType;
    private String content;

    private boolean isRead;
    private List<String> readBy;

    private String timestamp;


    // ======= GETTERS =======

    public String getId() {
        return id;
    }

    public String getBranchId() {
        return branchId;
    }

    public LoginResponse.User getSenderId() {
        return senderId;
    }

    public LoginResponse.User getReceiverId() {
        return receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public boolean isRead() {
        return isRead;
    }

    public List<String> getReadBy() {
        return readBy;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public static Message fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setSenderId(LoginResponse.User senderId) {
        this.senderId = senderId;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
