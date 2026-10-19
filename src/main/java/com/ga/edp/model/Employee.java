package com.ga.edp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Employee {

    private int id;
    private String name;
    private BigDecimal currentSalary;
    private LocalDate joinedDate;
    private String role;
    private double projectCompletion;
    private BigDecimal adjustedSalary;

    public Employee() {
    }

    public Employee(
            int id,
            String name,
            BigDecimal currentSalary,
            LocalDate joinedDate,
            String role,
            double projectCompletion
    ) {
        this.id = id;
        this.name = name;
        this.currentSalary = currentSalary;
        this.joinedDate = joinedDate;
        this.role = role;
        this.projectCompletion = projectCompletion;
        this.adjustedSalary = currentSalary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(BigDecimal currentSalary) {
        this.currentSalary = currentSalary;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getProjectCompletion() {
        return projectCompletion;
    }

    public void setProjectCompletion(double projectCompletion) {
        this.projectCompletion = projectCompletion;
    }

    public BigDecimal getAdjustedSalary() {
        return adjustedSalary;
    }

    public void setAdjustedSalary(BigDecimal adjustedSalary) {
        this.adjustedSalary = adjustedSalary;
    }
}
