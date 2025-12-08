package com.example.workio.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatHistory {
    @SerializedName("messages")
    private List<Message> messages;

    @SerializedName("pagination")
    private EmployeeListData.Pagination pagination;

    public List<Message> getMessages() {
        return messages;
    }

    public EmployeeListData.Pagination getPagination() {
        return pagination;
    }
}

