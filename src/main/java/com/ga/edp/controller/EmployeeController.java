
package com.ga.edp.controller;

import com.ga.edp.model.Employee;
import com.ga.edp.service.CsvReaderService;
import com.ga.edp.service.CsvWriterService;
import com.ga.edp.service.EmployeeProcessorService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final CsvReaderService csvReaderService;
    private final EmployeeProcessorService employeeProcessorService;
    private final CsvWriterService csvWriterService;

    public EmployeeController(
            CsvReaderService csvReaderService,
            EmployeeProcessorService employeeProcessorService,
            CsvWriterService csvWriterService) {
        this.csvReaderService = csvReaderService;
        this.employeeProcessorService = employeeProcessorService;
        this.csvWriterService = csvWriterService;
    }

    @PostMapping(
            value = "/process",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "text/csv"
    )
    public ResponseEntity<byte[]> processCsv(
            @RequestParam("file") MultipartFile file) throws IOException {

        List<Employee> employees = csvReaderService.readEmployees(file);
        employeeProcessorService.processEmployees(employees);
        byte[] output = csvWriterService.writeEmployees(employees);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("processed-employees.csv")
                                .build()
                                .toString())
                .body(output);
    }
}
