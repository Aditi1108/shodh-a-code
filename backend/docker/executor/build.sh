#!/bin/bash

# Build script for ShodhACode executor Docker image

echo "Building ShodhACode executor Docker image..."

# Build the Docker image
docker build -t shodhacode-executor:latest .

if [ $? -eq 0 ]; then
    echo "Successfully built shodhacode-executor:latest"
    echo ""
    echo "To test the executor image, run:"
    echo "  docker run --rm -it shodhacode-executor:latest"
    echo ""
    echo "Available languages:"
    echo "  - Java (openjdk-17)"
    echo "  - Python 3"
    echo "  - C/C++ (gcc/g++)"
    echo "  - JavaScript (Node.js)"
    echo "  - Go"
    echo "  - Rust"
    echo "  - Kotlin"
    echo "  - C# (Mono)"
else
    echo "Failed to build Docker image"
    exit 1
fi