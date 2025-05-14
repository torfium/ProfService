package com.profservice.model;

public class Position {
    private int positionId;
    private String positionName;
    private double baseSalary;
    private double bonusPercentage;
    private String description;

    // Геттеры и сеттеры
    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getBonusPercentage() { return bonusPercentage; }
    public void setBonusPercentage(double bonusPercentage) { this.bonusPercentage = bonusPercentage; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}