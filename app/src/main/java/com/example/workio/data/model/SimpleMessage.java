package com.example.workio.data.model;

import com.google.gson.annotations.SerializedName;

public class SimpleMessage {
    private String content;
    private String timestamp;
    private String senderName; // hoặc messageType tuỳ bạn đặt

    // ✅ Constructor rỗng (bắt buộc cho Gson)
    public SimpleMessage() {}

    // ✅ Constructor có tham số để bạn gọi thủ công
    public SimpleMessage(String content, String timestamp, String senderName) {
        this.content = content;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }
}


