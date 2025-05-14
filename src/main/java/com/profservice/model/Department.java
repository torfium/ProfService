package com.profservice.model;

public class Department {
    private int departmentId;
    private String departmentName;
    private Integer managerId;
    private String location;
    private String managerName;

    // Геттеры и сеттеры
    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
}