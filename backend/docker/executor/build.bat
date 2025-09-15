@echo off
REM Build script for ShodhACode executor Docker image (Windows)

echo Building ShodhACode executor Docker image...

REM Build the Docker image
docker build -t shodhacode-executor:latest .

if %ERRORLEVEL% EQU 0 (
    echo Successfully built shodhacode-executor:latest
    echo.
    echo To test the executor image, run:
    echo   docker run --rm -it shodhacode-executor:latest
    echo.
    echo Available languages:
    echo   - Java ^(openjdk-17^)
    echo   - Python 3
    echo   - C/C++ ^(gcc/g++^)
    echo   - JavaScript ^(Node.js^)
    echo   - Go
    echo   - Rust
    echo   - Kotlin
    echo   - C# ^(Mono^)
) else (
    echo Failed to build Docker image
    exit /b 1
)