
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EmployeeProcessorService {

    private static final int MAX_CONCURRENT_PROCESSING = 2;
    private static final int THREAD_POOL_SIZE = 4;

    private final SalaryCalculator salaryCalculator;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final Semaphore semaphore =
            new Semaphore(MAX_CONCURRENT_PROCESSING);

    private final ReentrantLock resultLock = new ReentrantLock();
    public EmployeeProcessorService(SalaryCalculator salaryCalculator) {
        this.salaryCalculator = salaryCalculator;
    }

    public List<Employee> processEmployees(List<Employee> employees) {
        List<Future<?>> futures = new ArrayList<>();
        List<Employee> results = new ArrayList<>();
        for (Employee employee : employees) {
            Future<?> future = executor.submit(() -> {
                boolean acquired = false;

                try {
                    semaphore.acquire();
                    acquired = true;

                    employee.setAdjustedSalary(
                            salaryCalculator.calculateAdjustedSalary(employee));

                    resultLock.lock();
                    try {
                        results.add(employee);
                    } finally {
                        resultLock.unlock();
                    }

                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(
                            "Employee processing was interrupted.", exception);

                } finally {
                    if (acquired) {
                        semaphore.release();
                    }
                }
            });

            futures.add(future);
        }

        try {
            for (Future<?> future : futures) {
                future.get();
            }

            return results;

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
