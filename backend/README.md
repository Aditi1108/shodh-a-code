# Shodh-a-Code Backend Service

Spring Boot backend for the competitive programming contest platform.

## 🏗️ Architecture

### Tech Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Container Runtime**: Docker

### Project Structure
```
backend/
├── contest/                    # Main application module
│   ├── src/main/java/com/shodhacode/
│   │   ├── config/            # Configuration classes
│   │   ├── controller/        # REST API endpoints
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA entities
│   │   ├── repository/       # Data repositories
│   │   └── service/          # Business logic
│   └── pom.xml
└── docker/
    └── executor/              # Code execution environment
        └── Dockerfile
```

## 🚀 Key Features

### Code Execution Engine
- **Secure Isolation**: Docker containers for each submission
- **Resource Limits**: CPU, memory, and time constraints
- **Multi-Language Support**: Java, Python3, C++, JavaScript
- **Process Management**: Java ProcessBuilder for Docker orchestration

### API Services
- Contest management
- User registration and tracking
- Code submission processing
- Real-time status updates
- Leaderboard calculation

## 📡 API Endpoints

### Contest Endpoints
- `GET /api/contests` - List all contests
- `GET /api/contests/{id}` - Get contest details
- `GET /api/contests/{id}/leaderboard` - Get contest leaderboard
- `POST /api/contests/join` - Join a contest

### Submission Endpoints
- `POST /api/submissions` - Submit code for evaluation
- `POST /api/submissions/run` - Test run without saving
- `GET /api/submissions/{id}` - Get submission status
- `GET /api/submissions/user/{userId}/contest/{contestId}` - User submissions

### User Endpoints
- `POST /api/users/register` - Register new user
- `GET /api/users/check/{username}` - Check username availability
- `GET /api/users/{id}` - Get user details

## 🔧 Configuration

### Application Properties (application.yml)
```yaml
# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /api

# Database Configuration
spring:
  datasource:
    url: jdbc:h2:mem:contestdb
  h2:
    console:
      enabled: true
      path: /h2-console

# Docker Configuration
docker:
  execution:
    enabled: true  # Docker execution is enabled
  debug:
    mode: true     # Preserves containers for debugging
  image:
    name: shodhacode-executor
```

### CORS Configuration
Configured to allow frontend on `http://localhost:5177`

## 🐳 Docker Integration

### Building the Executor Image
```bash
cd docker/executor
docker build -t code-executor .
```

### Executor Image Contents
- Ubuntu 22.04 base
- OpenJDK 17
- Python 3
- G++ compiler
- Node.js 18

## 🗄️ Database Schema

### Entities
1. **User**: User accounts and scores
2. **Contest**: Contest metadata and timing
3. **Problem**: Problem statements and constraints
4. **TestCase**: Input/output pairs for validation
5. **Submission**: Code submissions and results
6. **ContestParticipant**: User-contest relationships

### Data Initialization
Pre-populated with:
- 3 sample users (alice, bob, charlie)
- 2 active contests
- 6 problems with test cases

## 🔒 Security Measures

1. **Container Isolation**: Each submission runs in isolated container
2. **Network Disabled**: `--network none` for containers
3. **Resource Limits**: Strict CPU, memory, time constraints
4. **Input Validation**: All API inputs validated
5. **Temporary File Cleanup**: Automatic cleanup after execution

## 🏃 Running the Service

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker Desktop

### Start the Backend
```bash
mvn spring-boot:run
```

### Access Points
- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:contestdb`
  - Username: `sa`
  - Password: (empty)

## 📊 Submission Flow

1. **Receive Submission**: API endpoint validates request
2. **Queue Processing**: Added to SimpleQueueService
3. **File Creation**: Write code to temporary file
4. **Docker Execution**:
   - Mount code volume
   - Apply resource limits
   - Execute with timeout
5. **Output Validation**: Compare with expected output
6. **Status Update**: Update database and return result
7. **Cleanup**: Remove container and temp files

## 🧪 Testing

### Manual Testing
1. Use the frontend application
2. Submit code through the UI
3. Monitor logs for execution details

### API Testing
Use tools like Postman or curl with endpoints documented above

## 📈 Performance Considerations

- **Connection Pool**: HikariCP for database connections
- **Async Processing**: @Async for submission processing
- **In-Memory Database**: Fast for demo/development
- **Container Reuse**: Considered for production optimization

## 🔄 Future Improvements

1. **Production Database**: PostgreSQL/MySQL
2. **Message Queue**: RabbitMQ/Redis for submissions
3. **Caching Layer**: Redis for leaderboard
4. **Metrics**: Prometheus/Grafana monitoring
5. **Container Orchestration**: Kubernetes deployment

---

Built for the Shodh AI Full Stack Engineer Assessment