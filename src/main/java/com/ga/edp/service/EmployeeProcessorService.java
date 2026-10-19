
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeProcessorService {

    private final SalaryCalculator salaryCalculator;

    public EmployeeProcessorService(SalaryCalculator salaryCalculator) {
        this.salaryCalculator = salaryCalculator;
    }

    public List<Employee> processEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            employee.setAdjustedSalary(
                    salaryCalculator.calculateAdjustedSalary(employee));
        }

        return employees;
    }
}
