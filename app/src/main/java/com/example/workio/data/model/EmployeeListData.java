package com.example.workio.data.model;
import java.util.List;
public class EmployeeListData {
    private List<Employee> employees;
    private Pagination pagination;

    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private int totalItems;
        private int limit;

        // Getters
        public int getCurrentPage() { return currentPage; }
        public int getTotalPages() { return totalPages; }
        public int getTotalItems() { return totalItems; }
        public int getLimit() { return limit; }
    }

    // Getters
    public List<Employee> getEmployees() { return employees; }
    public Pagination getPagination() { return pagination; }
}