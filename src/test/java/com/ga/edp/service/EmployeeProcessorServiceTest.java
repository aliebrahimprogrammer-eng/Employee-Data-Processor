
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeProcessorServiceTest {

    private EmployeeProcessorService processorService;

    @BeforeEach
    void setUp() {
        processorService =
                new EmployeeProcessorService(new SalaryCalculator());
    }

    @AfterEach
    void tearDown() {
        processorService.shutdown();
    }

    @Test
    void processesAllEmployeesWithoutLosingRecords() {
        List<Employee> employees = createEmployees(30);

        List<Employee> results =
                processorService.processEmployees(employees);

        assertEquals(30, results.size());

        Set<Integer> resultIds = new HashSet<>();
        for (Employee employee : results) {
            resultIds.add(employee.getId());
        }

        assertEquals(30, resultIds.size());
    }

    @Test
    void calculatesAdjustedSalaryForEveryEmployee() {
        List<Employee> employees = createEmployees(20);

        List<Employee> results =
                processorService.processEmployees(employees);

        assertTrue(results.stream().allMatch(
                employee -> employee.getAdjustedSalary() != null));

        assertTrue(results.stream().allMatch(
                employee -> employee.getAdjustedSalary().signum() > 0));
    }

    @Test
    void reportsFailureWhenAnEmployeeHasAnUnsupportedRole() {
        Employee validEmployee = createEmployee(1, "Employee");
        Employee invalidEmployee = createEmployee(2, "Intern");

        assertThrows(
                IllegalStateException.class,
                () -> processorService.processEmployees(
                        List.of(validEmployee, invalidEmployee))
        );
    }

    private List<Employee> createEmployees(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(id -> createEmployee(id, "Employee"))
                .toList();
    }

    private Employee createEmployee(int id, String role) {
        return new Employee(
                id,
                "Employee " + id,
                new BigDecimal("1000.00"),
                LocalDate.now().minusYears(2),
                role,
                0.80
        );
    }
}
