
package com.ga.edp.controller;

import com.ga.edp.service.CsvReaderService;
import com.ga.edp.service.CsvWriterService;
import com.ga.edp.service.EmployeeProcessorService;
import com.ga.edp.service.SalaryCalculator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import({
        CsvReaderService.class,
        CsvWriterService.class,
        EmployeeProcessorService.class,
        SalaryCalculator.class
})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void uploadsCsvAndReturnsProcessedCsv() throws Exception {
        String csv = "1,Alice,1000.00,2020-01-01,Employee,0.80\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "employees.csv",
                "text/csv",
                csv.getBytes()
        );

        mockMvc.perform(multipart("/api/employees/process").file(file))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        org.hamcrest.Matchers.containsString(
                                "processed-employees.csv")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("adjustedSalary")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("Alice")));
    }

    @Test
    void rejectsEmptyCsvUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.csv",
                "text/csv",
                new byte[0]
        );

        mockMvc.perform(multipart("/api/employees/process").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
