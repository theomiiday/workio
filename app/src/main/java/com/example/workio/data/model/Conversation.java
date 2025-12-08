package com.example.workio.data.model;

public class Conversation {
    private LoginResponse.User user;                // người còn lại (nếu là chat cá nhân)
    private SimpleMessage lastMessage;
    private int unreadCount;

    // ✅ thêm các thuộc tính cho group chat
    private boolean isGroup;
    private String groupName;

    public Conversation() {
    }

    // ===== Getter & Setter cho user =====
    public LoginResponse.User getUser() {
        return user;
    }

    public void setUser(LoginResponse.User user) {
        this.user = user;
    }

    // ===== Getter & Setter cho lastMessage =====
    public SimpleMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(SimpleMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    // ===== Getter & Setter cho unreadCount =====
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    // ===== ✅ Các phương thức mới cho group chat =====
    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
