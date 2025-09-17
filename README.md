# Shodh-a-Code Contest Platform

A comprehensive competitive programming platform built with Spring Boot, React, and Docker. This platform enables users to participate in coding contests, submit solutions in multiple languages, and compete on live leaderboards.

## 🚀 Features

- **Live Coding Contests**: Join active contests and solve programming problems
- **Multi-Language Support**: Submit solutions in Java, Python3, C++, and JavaScript
- **Real-time Code Execution**: Secure Docker-based code execution with resource limits
- **Live Leaderboard**: Track rankings with automatic 15-second refresh when viewing
- **Asynchronous Processing**: Non-blocking submission processing with 2-second status polling
- **Test Case Validation**: Automatic validation against sample and hidden test cases

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
  "problems": [...]
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

## 🎯 Key Features Implementation

### Contest Participation Flow
1. User logs in with username
2. Browses active/upcoming contests
3. Joins contest (tracked in backend)
4. Solves problems with instant feedback
5. Tracks progress on leaderboard

### Submission Processing
1. Code submitted to backend
2. Added to processing queue
3. Docker container spawned
4. Code compiled/interpreted
5. Test cases executed
6. Results validated
7. Score and leaderboard updated

### Real-time Updates
- **Submission Status**: Polls every 2 seconds after submission
- **Live Leaderboard**:
  - Auto-refreshes every 15 seconds when viewing leaderboard tab
  - Fresh data loaded when switching to leaderboard
  - Smart polling - only active when tab is visible
- Immediate UI feedback for all user actions

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
- **2 Contests**: Active and Upcoming
- **6 Problems**: Various difficulty levels
- **Test Cases**: Both sample and hidden for each problem

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

## 🤝 Contributing

This is a demonstration project showcasing full-stack development capabilities with Spring Boot, React, and Docker integration.

---
