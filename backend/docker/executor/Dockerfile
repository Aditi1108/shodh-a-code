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

# Install GCC/G++ (for C and CPP)
RUN apt-get update && apt-get install -y gcc g++

# Install Go (for GO)
RUN wget -q https://go.dev/dl/go1.21.0.linux-amd64.tar.gz \
    && tar -C /usr/local -xzf go1.21.0.linux-amd64.tar.gz \
    && rm go1.21.0.linux-amd64.tar.gz
ENV PATH="/usr/local/go/bin:${PATH}"

# Install Rust (for RUST)
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"

# Install Kotlin (for KOTLIN)
RUN cd /opt \
    && wget -q https://github.com/JetBrains/kotlin/releases/download/v1.9.0/kotlin-compiler-1.9.0.zip \
    && apt-get update && apt-get install -y unzip \
    && unzip -q kotlin-compiler-1.9.0.zip \
    && rm kotlin-compiler-1.9.0.zip
ENV PATH="/opt/kotlinc/bin:${PATH}"

# Install Swift (for SWIFT)
# Note: Swift is large, so we're installing a minimal version
RUN apt-get update && apt-get install -y \
    binutils \
    git \
    gnupg2 \
    libc6-dev \
    libcurl4-openssl-dev \
    libedit2 \
    libgcc-9-dev \
    libpython3.8 \
    libsqlite3-0 \
    libstdc++-9-dev \
    libxml2-dev \
    libz3-dev \
    pkg-config \
    tzdata \
    zlib1g-dev \
    && wget -q https://download.swift.org/swift-5.9-release/ubuntu2204/swift-5.9-RELEASE/swift-5.9-RELEASE-ubuntu22.04.tar.gz \
    && tar xzf swift-5.9-RELEASE-ubuntu22.04.tar.gz \
    && mv swift-5.9-RELEASE-ubuntu22.04 /opt/swift \
    && rm swift-5.9-RELEASE-ubuntu22.04.tar.gz \
    && rm -rf /var/lib/apt/lists/*
ENV PATH="/opt/swift/usr/bin:${PATH}"

# Install C# via Mono (for CSHARP)
RUN apt-get update && apt-get install -y mono-complete

# Create a non-root user for running code
RUN useradd -m -u 1000 runner

# Set working directory
WORKDIR /code

# Default user (will be overridden by run command)
USER runner

# Default command
CMD ["/bin/bash"]