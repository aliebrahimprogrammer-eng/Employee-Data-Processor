
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvWriterService {

    public byte[] writeEmployees(List<Employee> employees) {
        StringBuilder csv = new StringBuilder();


        csv.append("id,name,currentSalary,joinedDate,role,projectCompletion,adjustedSalary\n");

        for (Employee employee : employees) {
            csv.append(employee.getId()).append(',')
                    .append(employee.getName()).append(',')
                    .append(employee.getCurrentSalary().toPlainString()).append(',')
                    .append(employee.getJoinedDate()).append(',')
                    .append(employee.getRole()).append(',')
                    .append(employee.getProjectCompletion()).append(',')
                    .append(employee.getAdjustedSalary().toPlainString())
                    .append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
