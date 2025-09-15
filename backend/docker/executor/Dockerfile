# Multi-language executor image for ShodhACode platform
FROM ubuntu:22.04

# Prevent interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Update and install essential packages
RUN apt-get update && apt-get install -y \
    # C/C++ compilers
    gcc \
    g++ \
    # Java
    openjdk-17-jdk \
    # Python 3
    python3 \
    python3-pip \
    # Node.js
    nodejs \
    npm \
    # Go
    golang-go \
    # Rust
    rustc \
    cargo \
    # Kotlin
    wget \
    unzip \
    # C#
    mono-complete \
    # Swift (basic support)
    # swift \
    # Utilities
    time \
    timeout \
    && rm -rf /var/lib/apt/lists/*

# Install Kotlin compiler
RUN wget -q https://github.com/JetBrains/kotlin/releases/download/v1.9.0/kotlin-compiler-1.9.0.zip \
    && unzip -q kotlin-compiler-1.9.0.zip -d /opt \
    && rm kotlin-compiler-1.9.0.zip \
    && ln -s /opt/kotlinc/bin/kotlinc /usr/local/bin/kotlinc \
    && ln -s /opt/kotlinc/bin/kotlin /usr/local/bin/kotlin

# Create a non-root user for running code
RUN useradd -m -u 1000 -s /bin/bash coderunner

# Set working directory
WORKDIR /code

# Set resource limits
RUN echo "coderunner soft nproc 32" >> /etc/security/limits.conf \
    && echo "coderunner hard nproc 32" >> /etc/security/limits.conf \
    && echo "coderunner soft nofile 64" >> /etc/security/limits.conf \
    && echo "coderunner hard nofile 64" >> /etc/security/limits.conf

# Switch to non-root user
USER coderunner

# Set environment variables for different languages
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="${PATH}:/usr/local/go/bin:/opt/kotlinc/bin"

# Default command
CMD ["/bin/bash"]