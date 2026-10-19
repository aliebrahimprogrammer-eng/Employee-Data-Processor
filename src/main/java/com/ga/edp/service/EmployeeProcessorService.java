
package com.ga.edp.service;

import com.ga.edp.model.Employee;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeProcessorService {

    private static final int THREAD_POOL_SIZE = 4;

    private final SalaryCalculator salaryCalculator;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public EmployeeProcessorService(SalaryCalculator salaryCalculator) {
        this.salaryCalculator = salaryCalculator;
    }

    public List<Employee> processEmployees(List<Employee> employees) {
        List<Future<?>> futures = new ArrayList<>();

        for (Employee employee : employees) {
            Future<?> future = executor.submit(() -> {
                employee.setAdjustedSalary(
                        salaryCalculator.calculateAdjustedSalary(employee));
            });

            futures.add(future);
        }

        try {
            for (Future<?> future : futures) {
                future.get();
            }

            return employees;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Waiting for employee processing was interrupted.",
                    exception);

        } catch (ExecutionException exception) {
            throw new IllegalStateException(
                    "Failed to process an employee.",
                    exception.getCause());
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException exception) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
