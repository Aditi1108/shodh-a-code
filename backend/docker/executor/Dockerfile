# Multi-language executor Docker image for Shodh-a-Code platform
FROM ubuntu:22.04

# Prevent interactive prompts during installation
ENV DEBIAN_FRONTEND=noninteractive

# Update and install essential packages
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    wget \
    time \
    && rm -rf /var/lib/apt/lists/*

# Install Java 17 (for JAVA)
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Install Python 3 (for PYTHON3)
RUN apt-get update && apt-get install -y python3 python3-pip

# Install Node.js (for JAVASCRIPT)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

# Install G++ (for CPP)
RUN apt-get update && apt-get install -y g++

# Create a non-root user for running code
RUN useradd -m -u 1000 runner

# Set working directory
WORKDIR /code

# Default user (will be overridden by run command)
USER runner

# Default command
CMD ["/bin/bash"]