# Employee Data Processor (EDP)

A Spring Boot REST API that accepts an employee CSV file, calculates salary adjustments, processes employees concurrently, and returns a the results.

## Technologies

- Java 17
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Java Executor framework
- `ReentrantLock`, `Semaphore`, and `Thread Pooling`
- JUnit and Spring MVC testing support

## Requirements

- JDK 17
- Maven
- An API client such as Postman or `curl`

## Setup

1. Clone or download the project.
2. Open the project in IntelliJ IDEA.
3. Confirm that Java 17 is configured as the project SDK.
4. Load the Maven project and resolve its dependencies.
5. Run the automated tests:

   ```bash
   mvn test
   ```

6. Start the application:

   ```bash
   mvn spring-boot:run
   ```

The application runs on port 8080 by default.

## CSV Input Format

The input CSV must contain six columns in this order, without a header row:

| Column | Description | Example |
|---|---|---|
| 1 | Employee ID | `1` |
| 2 | Employee name | `Alice` |
| 3 | Current salary | `52000.0` |
| 4 | Joining date (`yyyy-MM-dd`) | `2019-05-12` |
| 5 | Role | `Employee` |
| 6 | Project completion (`0.0`–`1.0`) | `0.8` |

Example:

```csv
1,Alice,52000.0,2019-05-12,Employee,0.8
2,Bob,60000.0,2020-01-01,Manager,0.9
3,Carol,75000.0,2018-06-15,Director,0.7
```

Employee IDs must be positive and unique. Salaries must be non-negative, roles must be `Director`, `Manager`, or `Employee`, project completion must be between 0.0 and 1.0, and joining dates cannot be in the future.

## API Usage

### Process an employee CSV

- **Method:** `POST`
- **Endpoint:** `/api/employees/process`
- **Content-Type:** `multipart/form-data`
- **File field:** `file`
- **Success response:** Downloadable `processed-employees.csv`

The response contains a CSV with the original employee details and the adjusted salary.

### Error responses

- `400 Bad Request`: Empty CSV or invalid employee data.
- `413 Payload Too Large`: Upload exceeds the configured size limit.
- `500 Internal Server Error`: Employee processing fails.

The default maximum file and request sizes are configured as 5 MB in `application.properties`.

## Salary Calculation Rules

Employees with project completion below 60% receive no salary increase.

Eligible employees receive the role-based increase plus an additional 2% for each completed year of service.

| Role | Base increase |
|---|---:|
| Director | 5% |
| Manager | 2% |
| Employee | 1% |

**Formula:**

Total increase percentage = Role increase + (Completed years × 2%)

Adjusted salary = Current salary × (1 + Total increase percentage)

Only completed calendar years count toward the service increase. An employee who has not completed their first year receives no service-based increase.

The final salary is rounded to two decimal places.

## Concurrency Implementation

The application demonstrates several Java concurrency mechanisms:

- **ExecutorService:** A fixed pool of four worker threads processes employee tasks.
- **Semaphore:** Limits concurrent entry into the controlled processing section to two tasks.
- **ReentrantLock:** Protects writes to the shared result collection.
- **Future:** Allows the application to wait for submitted tasks and detect failures before generating the output.

The executor is reused across requests and shut down when the Spring application is destroyed.

## Testing

Run the complete test suite:

```bash
mvn test
```

The tests cover salary calculation unit tests, concurrent processor test and upload tests.

## Current Limitations

- The CSV parser uses simple comma-separated parsing and does not support commas inside quoted input fields.
- The application processes uploads synchronously within the HTTP request.
- The application does not persist employee records to a database.
- The project does not include a frontend; CSV processing is performed through the REST API.

## Git History

Development is tracked through incremental Git commits with descriptive messages. Each commit represents a meaningful implementation, testing, validation, or documentation change.
