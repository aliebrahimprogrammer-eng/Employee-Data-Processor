
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

    public List<Employee> readEmployees(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file must not be empty.");
        }

        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                String[] columns = line.split(",", -1);

                if (columns.length != 6) {
                    throw new IllegalArgumentException(
                            "Invalid CSV at line " + lineNumber
                                    + ": expected 6 columns, found " + columns.length);
                }

                try {
                    int id = Integer.parseInt(columns[0].trim());
                    String name = columns[1].trim();
                    BigDecimal salary = new BigDecimal(columns[2].trim());
                    LocalDate joinedDate = LocalDate.parse(columns[3].trim());
                    String role = columns[4].trim();
                    double completion = Double.parseDouble(columns[5].trim());

                    employees.add(new Employee(
                            id, name, salary, joinedDate, role, completion));

                } catch (NumberFormatException | DateTimeParseException exception) {
                    throw new IllegalArgumentException(
                            "Invalid data at CSV line " + lineNumber + ": "
                                    + exception.getMessage(), exception);
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException(
                            "Invalid data at CSV line " + lineNumber + ": "
                                    + exception.getMessage(), exception);
                }
            }
        }

        if (employees.isEmpty()) {
            throw new IllegalArgumentException("CSV contains no employee records.");
        }

        return employees;
    }
}
