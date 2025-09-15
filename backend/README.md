# Shodh-a-Code Backend

A Spring Boot-based competitive programming platform backend that supports multiple programming languages, real-time code execution, and contest management.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Local Setup](#local-setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Features

- **Multi-language Support**: Java, Python3, C++, C, JavaScript, C#, Go, Rust, Kotlin, Swift
- **Contest Management**: Create and manage programming contests
- **Real-time Code Execution**: Docker-based sandboxed execution environment
- **Partial Scoring**: Score based on test cases passed
- **Asynchronous Processing**: Queue-based submission processing
- **H2 Database Console**: Built-in database viewer
- **CORS Enabled**: Ready for frontend integration

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Database**: H2 (In-memory for development)
- **Build Tool**: Maven
- **Java Version**: 17
- **Code Execution**: Docker (optional)
- **Queue**: ConcurrentLinkedQueue for async processing

## Prerequisites

1. **Java 17** or higher
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Docker** (Optional - for code execution)
   ```bash
   docker --version
   ```

## Local Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/shodh-a-code.git
cd shodh-a-code/backend/contest
```

### 2. Set JAVA_HOME Environment Variable

#### Windows:
```bash
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

#### Linux/Mac:
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### 3. Build the Project
```bash
mvn clean install
```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```

### Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/com/shodhacode/ContestApplication.java`
3. Right-click and select "Run 'ContestApplication'"

### Using Command Line
```bash
mvn clean package
java -jar target/contest-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
Currently, no authentication is implemented (development mode).

### API Endpoints

#### User Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/users` | Get all users | - |
| GET | `/users/{id}` | Get user by ID | - |
| POST | `/users/register` | Register new user | `{"username": "string", "email": "string"}` |
| GET | `/users/check/{username}` | Check if user exists | - |

#### Contest Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/contests` | Get all contests | - |
| GET | `/contests/{id}` | Get contest by ID | - |
| POST | `/contests` | Create new contest | Contest object |
| GET | `/contests/{id}/problems` | Get problems for contest | - |
| GET | `/contests/{id}/leaderboard` | Get contest leaderboard | - |

#### Problem Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/problems` | Get all problems | - |
| GET | `/problems/{id}` | Get problem by ID | - |
| POST | `/problems` | Create new problem | Problem object |

#### Submission Management

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| POST | `/submissions` | Submit code | `{"userId": 1, "problemId": 1, "code": "string", "language": "JAVA"}` |
| GET | `/submissions/{id}` | Get submission status | - |
| GET | `/submissions/user/{userId}/problem/{problemId}` | Get user's submissions for problem | - |
| GET | `/submissions/user/{userId}/problem/{problemId}/latest` | Get latest submission | - |
| GET | `/submissions/user/{userId}/contest/{contestId}` | Get user's contest submissions | - |

### Sample API Requests

#### Register User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com"
  }'
```

#### Submit Code
```bash
curl -X POST http://localhost:8080/api/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "problemId": 1,
    "code": "public class Solution {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}",
    "language": "JAVA"
  }'
```

## Database Schema

### Entities

#### User
- `id` (Long): Primary key
- `username` (String): Unique username
- `email` (String): User email
- `score` (Integer): Total score
- `problemsSolved` (Integer): Count of solved problems

#### Contest
- `id` (Long): Primary key
- `title` (String): Contest title
- `description` (String): Contest description
- `startTime` (LocalDateTime): Start time
- `endTime` (LocalDateTime): End time
- `isActive` (Boolean): Active status

#### Problem
- `id` (Long): Primary key
- `title` (String): Problem title
- `contest` (Contest): Associated contest
- `description` (String): Problem statement
- `inputFormat` (String): Input format description
- `outputFormat` (String): Output format description
- `points` (Integer): Maximum points
- `timeLimit` (Integer): Time limit in milliseconds
- `memoryLimit` (Integer): Memory limit in MB
- `testCases` (List<TestCase>): Associated test cases

#### TestCase
- `id` (Long): Primary key
- `problem` (Problem): Associated problem
- `isHidden` (Boolean): Visibility flag (false = sample, true = hidden)
- `timeLimit` (Integer): Time limit override
- `memoryLimit` (Integer): Memory limit override
- `input` (String): Test input
- `expectedOutput` (String): Expected output

#### Submission
- `id` (String): UUID primary key
- `user` (User): Submitting user
- `problem` (Problem): Target problem
- `status` (SubmissionStatus): Current status
- `language` (ProgrammingLanguage): Programming language
- `score` (Integer): Achieved score
- `testCasesPassed` (Integer): Passed test cases count
- `totalTestCases` (Integer): Total test cases count
- `submittedAt` (LocalDateTime): Submission timestamp
- `executionTime` (Long): Total execution time
- `code` (String): Submitted code
- `output` (String): Execution output
- `errorMessage` (String): Error details if any

### H2 Console Access

Access the H2 database console at: **http://localhost:8080/h2-console**

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (leave empty)

## Configuration

### Application Properties (`application.yml`)

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:contestdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

docker:
  execution:
    enabled: false  # Set to true if Docker is available
  image:
    name: shodhacode-executor
```

### Important Constants

Located in `ApplicationConstants.java`:
- `DEFAULT_TIME_LIMIT`: 2000ms
- `DEFAULT_MEMORY_LIMIT`: 256MB
- `DEFAULT_PROBLEM_POINTS`: 100
- `MAX_CODE_LENGTH`: 10000 characters

## Troubleshooting

### Port Already in Use
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Maven Build Issues
```bash
mvn clean
mvn dependency:purge-local-repository
mvn clean install
```

### JAVA_HOME Not Set
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# Linux/Mac
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Docker Not Available
If Docker is not installed, the application will return a runtime error for code execution. To disable Docker requirement:
1. Set `docker.execution.enabled=false` in `application.yml`
2. Code submissions will return mock results

## Development

### Adding New Programming Language
1. Add language to `ProgrammingLanguage` enum
2. Update `CodeExecutorService.getFileName()` method
3. Update `CodeExecutorService.getRunCommand()` method
4. Add language runtime to Docker executor image

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -Pprod
```

## Sample Data

The application initializes with sample data on startup:
- 3 users (alice, bob, charlie)
- 1 contest with 3 problems
- 8 test cases (mix of visible and hidden)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is part of the Shodh-a-Code platform.

## Support

For issues and questions, please create an issue in the GitHub repository.