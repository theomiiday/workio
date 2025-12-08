package com.example.workio.data.model;

public class SendMessageRequest {
    private String senderId;
    private String receiverId;
    private String content;

    public SendMessageRequest(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }
    public String getSenderId() {
        return senderId;
    }
}
