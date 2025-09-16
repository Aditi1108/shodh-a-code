#!/bin/bash

# Test User Registration
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "email": "test@example.com"
  }'

# Get All Users
curl http://localhost:8080/api/users

# Check if User Exists
curl http://localhost:8080/api/users/check/testuser1

# Submit Code
curl -X POST http://localhost:8080/api/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "problemId": 1,
    "code": "print(\"Hello World\")",
    "language": "PYTHON3"
  }'