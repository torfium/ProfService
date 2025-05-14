package com.profservice.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private Integer employeeId;

    public User(int userId, String username, String password, String role, String fullName, Integer employeeId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.employeeId = employeeId;
    }

    // Геттеры и сеттеры
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public Integer getEmployeeId() { return employeeId; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isManager() { return "MANAGER".equals(role); }
    public boolean isUser() { return "USER".equals(role); }
}