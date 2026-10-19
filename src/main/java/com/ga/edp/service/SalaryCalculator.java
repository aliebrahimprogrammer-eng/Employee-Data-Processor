
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
public class SalaryCalculator {

    private static final double MINIMUM_COMPLETION = 0.60;
    private static final double DIRECTOR_RATE = 0.05;
    private static final double MANAGER_RATE = 0.02;
    private static final double EMPLOYEE_RATE = 0.01;
    private static final double ANNUAL_SERVICE_RATE = 0.02;

    public BigDecimal calculateAdjustedSalary(Employee employee) {

        if (employee.getProjectCompletion() < MINIMUM_COMPLETION) {
            return employee.getCurrentSalary();
        }

        int completedYears = calculateCompletedYears(employee.getJoinedDate());
        double roleRate = getRoleRate(employee.getRole());

        double totalRate = roleRate + (completedYears * ANNUAL_SERVICE_RATE);

        BigDecimal increase = employee.getCurrentSalary()
                .multiply(BigDecimal.valueOf(totalRate));

        return employee.getCurrentSalary()
                .add(increase)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private int calculateCompletedYears(LocalDate joinedDate) {
        LocalDate today = LocalDate.now();

        if (joinedDate.isAfter(today)) {
            return 0;
        }

        return Period.between(joinedDate, today).getYears();
    }

    private double getRoleRate(String role) {
        return switch (role.trim().toLowerCase()) {
            case "director" -> DIRECTOR_RATE;
            case "manager" -> MANAGER_RATE;
            case "employee" -> EMPLOYEE_RATE;
            default -> throw new IllegalArgumentException(
                    "Unsupported employee role: " + role);
        };
    }
}
