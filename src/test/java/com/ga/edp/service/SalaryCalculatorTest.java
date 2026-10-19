
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalaryCalculatorTest {

    private SalaryCalculator salaryCalculator;

    @BeforeEach
    void setUp() {
        salaryCalculator = new SalaryCalculator();
    }

    @Test
    void employeeBelow60PercentReceivesNoIncrease() {
        Employee employee = createEmployee("Manager", 0.59, 0);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1000.00")));
    }

    @Test
    void employeeAt60PercentIsEligibleForIncrease() {
        Employee employee = createEmployee("Employee", 0.60, 0);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1010.00")));
    }

    @Test
    void directorReceivesFivePercentRoleIncrease() {
        Employee employee = createEmployee("Director", 0.80, 0);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1050.00")));
    }

    @Test
    void managerReceivesTwoPercentRoleIncrease() {
        Employee employee = createEmployee("Manager", 0.80, 0);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1020.00")));
    }

    @Test
    void employeeReceivesOnePercentRoleIncrease() {
        Employee employee = createEmployee("Employee", 0.80, 0);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1010.00")));
    }

    @Test
    void completedYearsAddTwoPercentPerYear() {
        Employee employee = createEmployee("Manager", 0.80, 2);

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        // 2% manager rate + (2 years * 2%) = 6%.
        assertEquals(0, result.compareTo(new BigDecimal("1060.00")));
    }

    @Test
    void lessThanOneCompletedYearAddsNoServiceIncrease() {
        Employee employee = createEmployee("Manager", 0.80, 0);
        employee.setJoinedDate(LocalDate.now().minusMonths(11));

        BigDecimal result =
                salaryCalculator.calculateAdjustedSalary(employee);

        assertEquals(0, result.compareTo(new BigDecimal("1020.00")));
    }

    private Employee createEmployee(
            String role,
            double completion,
            int completedYears) {

        return new Employee(
                1,
                "Test Employee",
                new BigDecimal("1000.00"),
                LocalDate.now().minusYears(completedYears),
                role,
                completion
        );
    }
}
