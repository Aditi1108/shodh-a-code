# Shodh-a-Code Contest Platform

A comprehensive competitive programming platform built with Spring Boot, React, and Docker. This platform enables users to participate in coding contests, submit solutions in multiple languages, and compete on live leaderboards.

## 🚀 Features Overview

- **Live Coding Contests**: Join active contests and solve programming problems
- **Multi-Language Support**: Submit solutions in Java, Python3, C++, and JavaScript
- **Real-time Code Execution**: Secure Docker-based code execution with resource limits
- **Live Leaderboard**: Track rankings with automatic 15-second refresh when viewing
- **Asynchronous Processing**: Non-blocking submission processing with 2-second status polling
- **Test Case Validation**: Automatic validation against sample and hidden test cases

## 📋 Detailed Features & Functionalities

### 👤 User Management
- **Simple Registration**: Enter username to create account automatically
- **Persistent Sessions**: User data stored in localStorage for seamless experience
- **Global Score Tracking**: Cumulative score across all contests
- **Problem Count**: Track total problems solved across platform

### 🏆 Contest System

#### Contest Types
- **Active Contests**: Currently running, open for participation
- **Ended Contests**: View-only mode with restricted submission

#### Contest Features
- **Join Contest**: One-click join with backend validation
- **Contest Timer**: Shows time remaining with visual indicators
- **Participant Count**: Real-time display of joined users
- **Contest-specific Scoring**: Separate scores per contest

### 📝 Problem Solving

#### Problem Display
- **Structured Layout**:
  - Problem statement with formatted description
  - Input/Output format specifications
  - Constraints clearly defined
  - Sample test cases visible
  - Points allocation shown

#### Code Editor
- **Language Selection**: Dropdown to switch between Java, Python3, C++, JavaScript
- **Pre-loaded Templates**: Language-specific boilerplate code with instructions
- **Monospace Font**: Code-friendly typography
- **Large Text Area**: Ample space for coding (adjustable)

#### Submission Types
1. **Run Code (Test Mode)**
   - Tests against sample cases only
   - Immediate feedback without scoring
   - Helps debug before final submission

2. **Submit Code (Final)**
   - Tests against all cases (sample + hidden)
   - Updates score and leaderboard
   - Counts toward contest ranking

### ⚡ Real-time Execution

#### Submission Processing
- **Status States**:
  - `PENDING`: In queue for processing
  - `RUNNING`: Currently executing
  - `ACCEPTED`: All test cases passed
  - `PARTIALLY_ACCEPTED`: Some cases passed
  - `WRONG_ANSWER`: Output mismatch
  - `TIME_LIMIT_EXCEEDED`: Execution timeout
  - `COMPILATION_ERROR`: Code syntax issues
  - `RUNTIME_ERROR`: Execution crashes

#### Feedback Display
- **Live Status Updates**: Poll every 2 seconds
- **Test Case Results**: Shows passed/failed count
- **Execution Time**: Display milliseconds taken
- **Error Messages**: Compilation/runtime errors shown
- **Score Calculation**: Points based on cases passed

### 🏅 Leaderboard System

#### Features
- **Real-time Rankings**: Updates every 15 seconds when viewing
- **Comprehensive Metrics**:
  - Rank position
  - Username and full name
  - Total score
  - Problems solved count
  - Last submission time

#### Smart Polling
- **Tab-aware**: Only polls when leaderboard tab is active
- **Automatic Refresh**: No manual reload needed
- **Efficient**: Stops polling when navigating away

### 📊 User Dashboard

#### Home Page Statistics
- **Personal Metrics**:
  - Total score across all contests
  - Total problems solved
  - Active contests count

#### Contest Cards
- **Active Contests**:
  - Join button if not participated
  - View button if already joined
  - Problem count display
  - End time shown

#### Auto-refresh
- **Page Focus Detection**: Updates when returning to tab
- **Visibility API**: Refreshes on page visibility change

### 📜 Submission History

#### Your Submissions Tab
- **Per-Contest History**: All attempts for current contest
- **Detailed Information**:
  - Problem title
  - Programming language used
  - Submission status
  - Score achieved
  - Timestamp
  - View code option

### 🔒 Security Features

#### Code Execution
- **Docker Isolation**: Each submission in separate container
- **Network Disabled**: No internet access during execution
- **Resource Limits**:
  - CPU: 1 core maximum
  - Memory: 128-256MB per problem
  - Time: 5 seconds for Java, varies by language

#### Input Protection
- **Size Limits**: Maximum code length enforced
- **Input Validation**: All API inputs sanitized
- **SQL Injection Prevention**: JPA parameterized queries

### 🔄 Asynchronous Processing

#### Queue Management
- **FIFO Processing**: Fair submission order
- **Non-blocking**: UI remains responsive
- **Status Polling**: Check result without page refresh

#### Error Handling
- **Graceful Failures**: Errors don't crash system
- **User Feedback**: Clear error messages
- **Retry Logic**: Automatic retry for transient failures

### 🎨 User Experience

#### Responsive Design
- **Mobile Support**: Touch-friendly interface
- **Tablet Optimization**: Efficient space usage
- **Desktop Layout**: Multi-column when space allows

#### Visual Feedback
- **Loading States**: Spinners during operations
- **Success Indicators**: Green checkmarks
- **Error Highlights**: Red error messages
- **Progress Tracking**: Status badges

### 📈 Performance Optimizations

#### Frontend
- **Efficient Polling**: Only when needed
- **Cleanup**: Proper interval management
- **Minimal Re-renders**: Optimized React components

#### Backend
- **In-Memory Database**: Fast for demo
- **Connection Pooling**: HikariCP optimization
- **Async Processing**: Non-blocking operations

## 📁 Project Structure

```
shodh-a-code/
├── backend/                 # Spring Boot backend
│   ├── contest/             # Main application module
│   │   ├── src/
│   │   └── pom.xml
│   └── docker/
│       └── executor/        # Docker execution environment
│           └── Dockerfile
├── frontend/                # React frontend
│   ├── src/
│   ├── package.json
│   └── tailwind.config.js
└── README.md
```

## 🛠️ Setup Instructions

### Prerequisites

- Java 17+
- Node.js 18+
- Docker Desktop
- Maven 3.6+

### Step 1: Build the Docker Execution Environment

```bash
cd backend/docker/executor
docker build -t code-executor .
```

This creates a Docker image with Java, Python3, C++, and Node.js runtimes for code execution.

### Step 2: Start the Backend

```bash
cd backend/contest
mvn spring-boot:run
```

The backend will start on `http://localhost:8080` with an H2 in-memory database pre-populated with sample contests and problems.

### Step 3: Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5177`

### Step 4: Access the Application

1. Navigate to `http://localhost:5177`
2. Enter any username to login (e.g., "alice","bob","charlie" or you can register and create your own username)
3. Browse contests and start solving problems!

## 📋 API Design

### Core Endpoints

#### 1. Get Contest Details
```http
GET /api/contests/{contestId}
```
**Response:**
```json
{
  "id": 1,
  "title": "Weekly Challenge #1",
  "description": "Test your skills",
  "startTime": "2024-01-01T10:00:00",
  "endTime": "2024-12-31T18:00:00",
  "isActive": true,
  "problems": []
}
```

#### 2. Submit Solution
```http
POST /api/submissions
```
**Request:**
```json
{
  "userId": 1,
  "problemId": 1,
  "code": "public class Solution {...}",
  "language": "JAVA"
}
```
**Response:**
```json
{
  "submissionId": "uuid-string",
  "status": "PENDING"
}
```

#### 3. Get Submission Status
```http
GET /api/submissions/{submissionId}
```
**Response:**
```json
{
  "submissionId": "uuid-string",
  "status": "ACCEPTED",
  "score": 100,
  "testCasesPassed": 3,
  "totalTestCases": 3,
  "executionTime": "150ms",
  "errorMessage": null
}
```

#### 4. Get Leaderboard
```http
GET /api/contests/{contestId}/leaderboard
```
**Response:**
```json
[
  {
    "rank": 1,
    "userId": 1,
    "username": "alice",
    "score": 300,
    "problemsSolved": 3,
    "lastSubmission": "2024-01-15T14:30:00"
  }
]
```

### Additional Endpoints

- `GET /api/contests` - List all contests
- `POST /api/contests/join` - Join a contest
- `POST /api/submissions/run` - Test run without submission
- `GET /api/submissions/user/{userId}/contest/{contestId}` - User's submissions
- `GET /api/languages` - Supported programming languages

## 🏗️ Design Choices & Architecture

### Backend Architecture

#### Service Layer Design
The backend follows a **layered architecture** with clear separation of concerns:

- **Controller Layer**: RESTful endpoints with request/response DTOs
- **Service Layer**: Business logic, including `CodeExecutorService` for Docker orchestration
- **Repository Layer**: JPA repositories for data persistence
- **Entity Layer**: Domain models with JPA annotations

#### Code Execution Engine
**Challenge**: Safely executing untrusted user code with proper isolation and resource limits.

**Solution**:
- Docker containers provide complete isolation
- ProcessBuilder manages Docker commands programmatically
- Resource limits (CPU, memory, time) prevent abuse
- Automatic cleanup ensures no resource leaks

**Trade-offs**:
- **Pros**: Strong security, language flexibility, resource control
- **Cons**: Docker dependency, container startup overhead

#### Queue Management
Implemented a `SimpleQueueService` for submission processing:
- In-memory queue for simplicity
- Async processing with `@Async` annotation
- Could scale to Redis/RabbitMQ for production

### Frontend Architecture

#### State Management
**Choice**: Zustand for global state management

**Reasoning**:
- Lightweight (8kb) compared to Redux
- Simple API with hooks
- TypeScript support out of the box
- Perfect for medium-sized applications

**State Structure**:
```typescript
{
  user: User | null,
  selectedLanguage: ProgrammingLanguage,
  joinedContests: Set<number>
}
```

#### Asynchronous Handling
**Polling Strategy**:
- **Submission Status**: 2-second intervals until completion
- **Live Leaderboard**:
  - Polls every 15 seconds when leaderboard tab is active
  - Immediate refresh when switching to leaderboard tab
  - Stops polling when navigating away (efficient resource usage)
- Automatic cleanup on component unmount
- Visual feedback during all async operations

**Component Structure**:
- `Contest.tsx`: Main contest hub with tabs
- `Problem.tsx`: Code editor and submission logic
- `Leaderboard.tsx`: Reusable leaderboard component

### DevOps & Docker Integration

#### Dockerfile Optimization
- Multi-stage build not needed (execution only)
- Minimal Ubuntu base for smaller image
- Pre-installed language runtimes
- Non-root user for security

#### Execution Flow
1. Write user code to temporary file
2. Execute Docker run with mounted volume
3. Pipe test input via stdin
4. Capture stdout and compare
5. Clean up container and files

**Security Measures**:
- Network isolation (`--network none`)
- Memory limits (`--memory`)
- CPU limits (`--cpus`)
- Time limits (`timeout` command)
- Read-only filesystem where possible

## 🔒 Security Considerations

1. **Code Execution**: Fully isolated in Docker containers
2. **Resource Limits**: Strict CPU, memory, and time constraints
3. **Input Validation**: All API inputs validated
4. **Authentication**: Simplified for demo (production would need JWT)
5. **SQL Injection**: Protected via JPA/Hibernate

## 📈 Scalability Considerations

### Current Limitations
- In-memory H2 database (switch to PostgreSQL)
- Single-instance processing (add worker nodes)
- Synchronous Docker execution (use message queue)

### Production Improvements
1. PostgreSQL/MySQL for persistence
2. Redis for caching and queues
3. Kubernetes for container orchestration
4. Load balancer for multiple instances
5. CDN for static assets

## 🧪 Testing the Application

### Sample Test Flow
1. Login with any username
2. Navigate to "Weekly Challenge #1"
3. Select "Two Sum" problem
4. Submit this solution:
```python
n = int(input())
arr = list(map(int, input().split()))
target = int(input())

for i in range(n):
    for j in range(i+1, n):
        if arr[i] + arr[j] == target:
            print(f"{i} {j}")
            exit()
```
5. Watch status update from PENDING → RUNNING → ACCEPTED
6. Check leaderboard for updated rankings

### Pre-populated Data
- **3 Contests**:
  - Daily Challenge #1 (Active, 3 problems)
  - Weekly Coding Challenge #2 (Active, 3 problems)
  - Past Challenge #1 (Ended, 2 problems - view only, with leaderboard)
- **8 Problems**: Various difficulty levels
- **Test Cases**: Both sample and hidden for each problem
- **4 Sample Users**: alice, bob, charlie, dylan
- **Ended Contest Leaderboard**:
  - Alice: 125 points (2 problems solved)
  - Bob: 75 points (1 problem solved)
  - Charlie: 50 points (1 problem solved)
  - Dylan: 0 points (attempted but failed)

## 🚦 Development Decisions & Trade-offs

### Why Spring Boot?
- Rapid development with auto-configuration
- Excellent JPA/Hibernate integration
- Built-in async support
- Strong ecosystem for REST APIs

### Why React over Next.js?
- Simpler deployment model
- No SSR requirements for this use case
- Faster development iteration
- Client-side routing sufficient

### Why Docker for Code Execution?
- **Alternatives Considered**:
  - VM (too heavy)
  - Process isolation (insufficient security)
  - Serverless functions (complex setup)
- **Docker Benefits**:
  - Perfect balance of isolation and performance
  - Easy local development
  - Consistent across environments

## 📝 Future Enhancements

1. **WebSocket Integration**: Replace polling with real-time updates
2. **Code Templates**: Language-specific boilerplate
3. **Problem Tags**: Categorize by difficulty and topic
4. **User Profiles**: Track historical performance
5. **Admin Panel**: Contest and problem management
6. **Plagiarism Detection**: Code similarity checking

## 🔧 Troubleshooting

### Common Issues and Solutions

#### 1. Docker Execution Issues

**Problem**: "Code execution environment not available" error
```
Docker execution is disabled. Cannot execute submission
```

**Solutions**:
- Ensure Docker Desktop is installed and running
- Build the executor image:
  ```bash
  cd backend/docker/executor
  docker build -t code-executor .
  ```
- Verify Docker is accessible:
  ```bash
  docker version
  docker images | grep code-executor
  ```

**Problem**: "Exit code 124" or timeout errors
```
Error: Unknown error (exit code: 124)
```

**Solutions**:
- This indicates a timeout. The default time limits are problem-specific (usually 1-5 seconds)
- Check if your code has infinite loops
- Java compilation time is included in the time limit

#### 2. Java Submission Errors

**Problem**: "class FizzBuzz is public, should be declared in a file named FizzBuzz.java"

**Solution**:
- Your main class MUST be named `Solution` (not Main, not the problem name)
- Correct format:
  ```java
  public class Solution {
      public static void main(String[] args) {
          // Your code here
      }
  }
  ```

#### 3. Frontend Issues

**Problem**: Frontend not loading or API connection errors

**Solutions**:
- Ensure backend is running on `http://localhost:8080`
- Frontend should be on `http://localhost:5177`
- Check CORS is configured correctly in backend
- Clear browser cache and localStorage

#### 4. Database Issues

**Problem**: H2 console not accessible

**Solution**:
- Access at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:contestdb`
- Username: `sa`
- Password: (leave empty)

**Problem**: Data not persisting after restart

**Solution**:
- H2 is configured as in-memory database
- Data is re-initialized on each startup from `DataInitializer.java`
- For persistent storage, switch to PostgreSQL/MySQL in production

#### 5. Submission Processing Issues

**Problem**: Submissions stuck in PENDING status

**Solutions**:
- Check backend logs for queue processing errors
- Verify Docker is running and accessible
- Check SimpleQueueService logs:
  ```
  grep "Queue processor" backend.log
  grep "Added submission" backend.log
  ```

**Problem**: Incorrect scores or leaderboard not updating

**Solutions**:
- Scores are calculated as: (test cases passed / total test cases) × problem points
- Leaderboard updates every 15 seconds when viewing the leaderboard tab
- Check ContestParticipant records in H2 console

#### 6. Build and Setup Issues

**Problem**: Maven build fails

**Solutions**:
- Ensure Java 17+ is installed:
  ```bash
  java -version
  ```
- Clear Maven cache:
  ```bash
  mvn clean
  mvn dependency:purge-local-repository
  ```

**Problem**: npm install fails

**Solutions**:
- Ensure Node.js 18+ is installed:
  ```bash
  node --version
  ```
- Clear npm cache:
  ```bash
  npm cache clean --force
  rm -rf node_modules package-lock.json
  npm install
  ```

#### 7. Contest and Problem Access

**Problem**: "Contest has ended" message when trying to submit

**Solution**:
- Check contest end time in the database
- Ended contests are view-only (Past Challenge #1 is intentionally ended)
- Only active contests accept submissions

**Problem**: No test cases showing for problems

**Solution**:
- Verify test cases exist in database
- Sample test cases (non-hidden) are shown to users
- Hidden test cases only run during final submission

### Debugging Tips

#### Enable Detailed Logging

Backend logging is configured in `application.yml`:
```yaml
logging:
  level:
    com.shodhacode: DEBUG
    org.springframework: INFO
```

#### Check Service Status

1. **Backend Health Check**:
   ```bash
   curl http://localhost:8080/api/contests
   ```

2. **Docker Status**:
   ```bash
   docker ps  # Check running containers
   docker images | grep code-executor  # Verify image exists
   ```

3. **Queue Processing**:
   - Look for these log messages:
   ```
   Queue processor started with 4 worker threads
   Added submission to queue
   Processing submission
   Execution completed
   ```

#### Common Log Patterns to Check

```bash
# Check for Docker issues
grep -i "docker" logs/backend.log

# Check submission processing
grep "submission.*RUNNING\|ACCEPTED\|WRONG_ANSWER" logs/backend.log

# Check for errors
grep -i "error\|exception" logs/backend.log

# Monitor queue activity
tail -f logs/backend.log | grep -i "queue\|submission"
```

### Performance Optimization

If experiencing slow performance:

1. **Increase JVM heap size**:
   ```bash
   java -Xmx2048m -jar backend/contest/target/contest-0.0.1-SNAPSHOT.jar
   ```

2. **Adjust thread pool size** in `SimpleQueueService.java`:
   ```java
   executorService = Executors.newFixedThreadPool(8); // Increase from 4
   ```

3. **Optimize Docker** resource limits in `CodeExecutorService.java`:
   ```java
   commandParts.add("--memory=256m"); // Increase if needed
   ```

## 📚 Additional Documentation

For more detailed information about specific components:
- **Backend Documentation**: See [backend/README.md](backend/README.md) for Spring Boot architecture, API details, and Docker configuration
- **Frontend Documentation**: See [frontend/README.md](frontend/README.md) for React components, state management, and UI implementation

## 🤝 Contributing

This is a demonstration project showcasing full-stack development capabilities with Spring Boot, React, and Docker integration.

---
