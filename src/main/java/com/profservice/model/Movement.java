package com.profservice.model;

import java.time.LocalDate;

public class Movement {
    private int movementId;
    private int employeeId;
    private String movementType;
    private LocalDate movementDate;
    private Integer previousDepartmentId;
    private Integer newDepartmentId;
    private Integer previousPositionId;
    private Integer newPositionId;
    private Double salaryChange;
    private String reason;
    private String documentReference;
    private int responsibleId;
    private String employeeName;
    private String previousDepartmentName;
    private String newDepartmentName;
    private String previousPositionName;
    private String newPositionName;
    private String responsibleName;

    // Геттеры и сеттеры
    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public LocalDate getMovementDate() { return movementDate; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }
    public Integer getPreviousDepartmentId() { return previousDepartmentId; }
    public void setPreviousDepartmentId(Integer previousDepartmentId) { this.previousDepartmentId = previousDepartmentId; }
    public Integer getNewDepartmentId() { return newDepartmentId; }
    public void setNewDepartmentId(Integer newDepartmentId) { this.newDepartmentId = newDepartmentId; }
    public Integer getPreviousPositionId() { return previousPositionId; }
    public void setPreviousPositionId(Integer previousPositionId) { this.previousPositionId = previousPositionId; }
    public Integer getNewPositionId() { return newPositionId; }
    public void setNewPositionId(Integer newPositionId) { this.newPositionId = newPositionId; }
    public Double getSalaryChange() { return salaryChange; }
    public void setSalaryChange(Double salaryChange) { this.salaryChange = salaryChange; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDocumentReference() { return documentReference; }
    public void setDocumentReference(String documentReference) { this.documentReference = documentReference; }
    public int getResponsibleId() { return responsibleId; }
    public void setResponsibleId(int responsibleId) { this.responsibleId = responsibleId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getPreviousDepartmentName() { return previousDepartmentName; }
    public void setPreviousDepartmentName(String previousDepartmentName) { this.previousDepartmentName = previousDepartmentName; }
    public String getNewDepartmentName() { return newDepartmentName; }
    public void setNewDepartmentName(String newDepartmentName) { this.newDepartmentName = newDepartmentName; }
    public String getPreviousPositionName() { return previousPositionName; }
    public void setPreviousPositionName(String previousPositionName) { this.previousPositionName = previousPositionName; }
    public String getNewPositionName() { return newPositionName; }
    public void setNewPositionName(String newPositionName) { this.newPositionName = newPositionName; }
    public String getResponsibleName() { return responsibleName; }
    public void setResponsibleName(String responsibleName) { this.responsibleName = responsibleName; }
}
