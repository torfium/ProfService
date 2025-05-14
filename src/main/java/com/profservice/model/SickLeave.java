package com.profservice.model;

import java.time.LocalDate;

public class SickLeave {
    private int sickLeaveId;
    private int employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sickLeaveNumber;
    private String diagnosis;
    private int paymentPercentage;
    private String status;
    private String employeeName;

    // Геттеры и сеттеры
    public int getSickLeaveId() { return sickLeaveId; }
    public void setSickLeaveId(int sickLeaveId) { this.sickLeaveId = sickLeaveId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getSickLeaveNumber() { return sickLeaveNumber; }
    public void setSickLeaveNumber(String sickLeaveNumber) { this.sickLeaveNumber = sickLeaveNumber; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public int getPaymentPercentage() { return paymentPercentage; }
    public void setPaymentPercentage(int paymentPercentage) { this.paymentPercentage = paymentPercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}