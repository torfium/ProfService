package com.profservice.model;

import java.time.LocalDate;

public class Hiring {
    private int hiringId;
    private int employeeId;
    private LocalDate hireDate;
    private String contractNumber;
    private double salary;
    private Integer probationPeriod;
    private int hrManagerId;
    private String employeeName;
    private String hrManagerName;

    // Геттеры и сеттеры
    public int getHiringId() { return hiringId; }
    public void setHiringId(int hiringId) { this.hiringId = hiringId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public Integer getProbationPeriod() { return probationPeriod; }
    public void setProbationPeriod(Integer probationPeriod) { this.probationPeriod = probationPeriod; }
    public int getHrManagerId() { return hrManagerId; }
    public void setHrManagerId(int hrManagerId) { this.hrManagerId = hrManagerId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getHrManagerName() { return hrManagerName; }
    public void setHrManagerName(String hrManagerName) { this.hrManagerName = hrManagerName; }
}