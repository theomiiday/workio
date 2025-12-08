package com.example.workio.data.model;

import java.util.List;

public class NotificationResponse {
    private List<Notification> notifications;
    private Pagination pagination;
    private int unreadCount;

    public List<Notification> getNotifications() { return notifications; }
    public Pagination getPagination() { return pagination; }
    public int getUnreadCount() { return unreadCount; }

    public static class Pagination {
        private int total;
        private int page;
        private int limit;
        private int totalPages;

        public int getTotal() { return total; }
        public int getPage() { return page; }
        public int getLimit() { return limit; }
        public int getTotalPages() { return totalPages; }
    }
}
