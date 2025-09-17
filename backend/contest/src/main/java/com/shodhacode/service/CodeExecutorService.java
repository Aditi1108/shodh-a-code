package com.shodhacode.service;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.*;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutorService {

    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    
    @Value("${docker.execution.enabled:false}")
    private boolean dockerEnabled;
    
    @Value("${docker.image.name:" + ApplicationConstants.DEFAULT_DOCKER_IMAGE + "}")
    private String dockerImageName = ApplicationConstants.DEFAULT_DOCKER_IMAGE;
    
    @Value("${execution.temp.dir:}")
    private String tempDir;

    @Value("${docker.debug.mode:false}")
    private boolean dockerDebugMode;
    
    @PostConstruct
    public void init() {
        // Use system temp directory if not configured
        if (tempDir == null || tempDir.isEmpty()) {
            tempDir = System.getProperty("java.io.tmpdir") + "shodhacode";
        }
        
        // Ensure directory exists
        try {
            Files.createDirectories(Paths.get(tempDir));
        } catch (IOException e) {
            log.error("Failed to create temp directory: {}", e.getMessage());
        }
        
        log.info("=================================================");
        log.info("CodeExecutorService initialization:");
        log.info("Docker execution enabled: {}", dockerEnabled);
        log.info("Docker image name: {}", dockerImageName);
        log.info("Temp directory: {}", tempDir);
        log.info("Docker debug mode: {}", dockerDebugMode);
        
        if (dockerEnabled) {
            checkDockerAvailability();
        } else {
            log.warn("Docker execution is DISABLED. Code execution will not work!");
        }
        log.info("=================================================");
    }
    
    private void checkDockerAvailability() {
        try {
            // Check if Docker is running
            ProcessBuilder dockerCheckBuilder = new ProcessBuilder("docker", "version");
            // Redirect error stream to output stream to capture all output
            dockerCheckBuilder.redirectErrorStream(true);
            Process dockerCheck = dockerCheckBuilder.start();
            boolean dockerRunning = dockerCheck.waitFor(5, TimeUnit.SECONDS);
            
            if (dockerRunning && dockerCheck.exitValue() == 0) {
                log.info("✓ Docker is available and running");
                
                // Check if the required image exists
                ProcessBuilder imageCheckBuilder = new ProcessBuilder("docker", "images", "-q", dockerImageName);
                imageCheckBuilder.redirectErrorStream(true);
                Process imageCheck = imageCheckBuilder.start();
                imageCheck.waitFor(5, TimeUnit.SECONDS);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(imageCheck.getInputStream()));
                String imageId = reader.readLine();
                
                if (imageId != null && !imageId.trim().isEmpty()) {
                    log.info("✓ Docker executor image '{}' found (ID: {})", dockerImageName, imageId.substring(0, Math.min(12, imageId.length())));
                } else {
                    log.error("✗ Docker executor image '{}' NOT FOUND! Please build it using:", dockerImageName);
                    log.error("  cd backend/docker/executor && docker build -t {} .", dockerImageName);
                    dockerEnabled = false;
                }
            } else {
                log.error("✗ Docker is NOT running or not installed!");
                dockerEnabled = false;
            }
        } catch (Exception e) {
            log.error("✗ Failed to check Docker availability: {}", e.getMessage());
            dockerEnabled = false;
        }
    }

    public void executeCode(Submission submission) {
        log.info("Starting code execution for submission {} with language {}",
                 submission.getId(), submission.getLanguage());
        submission.setStatus(SubmissionStatus.RUNNING);
        submissionRepository.save(submission);

        try {
            Problem problem = submission.getProblem();
            if (problem == null) {
                throw new RuntimeException("Problem not found for submission");
            }

            if (dockerEnabled) {
                executeWithDocker(submission, problem);
            } else {
                // Docker not enabled - return system error
                submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
                submission.setErrorMessage("Code execution environment not available. Please contact administrator.");
                submission.setScore(0);
                submission.setTestCasesPassed(0);
                submission.setTotalTestCases(problem.getTestCases().size());
                submissionRepository.save(submission);
                log.error("Docker execution is disabled. Cannot execute submission {}", submission.getId());
            }
            
        } catch (Exception e) {
            log.error("Error executing submission {}: {}", submission.getId(), e.getMessage(), e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Execution failed: " + e.getMessage());
            submissionRepository.save(submission);
        }
    }
    
    private void executeWithDocker(Submission submission, Problem problem) throws Exception {
        String executionId = UUID.randomUUID().toString();
        Path workDir = Paths.get(tempDir, executionId);
        Files.createDirectories(workDir);

        log.info("Executing submission {} with language: {}", submission.getId(), submission.getLanguage());

        try {
            String fileName = getFileName(submission.getLanguage());
            Path codeFile = workDir.resolve(fileName);
            Files.write(codeFile, submission.getCode().getBytes());

            log.info("Created file {} for submission {}", fileName, submission.getId());
            
            List<TestCase> testCases = problem.getTestCases();
            if (testCases == null || testCases.isEmpty()) {
                log.error("No test cases found for problem {}", problem.getId());
                submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
                submission.setErrorMessage("No test cases available for this problem");
                submission.setScore(0);
                submission.setTestCasesPassed(0);
                submission.setTotalTestCases(0);
                submissionRepository.save(submission);
                return;
            }
            
            // Sort test cases: non-hidden (visible) first, then hidden
            List<TestCase> sortedTestCases = new ArrayList<>();
            testCases.stream()
                .filter(tc -> !tc.getIsHidden())
                .forEach(sortedTestCases::add);
            testCases.stream()
                .filter(tc -> tc.getIsHidden())
                .forEach(sortedTestCases::add);
            
            int testCasesPassed = 0;
            int totalTestCases = sortedTestCases.size();
            int pointsPerTestCase = problem.getPoints() / totalTestCases;
            int totalScore = 0;
            StringBuilder output = new StringBuilder();
            long totalExecutionTime = 0;
            
            for (int i = 0; i < sortedTestCases.size(); i++) {
                TestCase testCase = sortedTestCases.get(i);
                String testCaseLabel = testCase.getIsHidden() ? "Hidden test case " : "Sample test case ";
                log.info("Running {} {} for submission {}", testCaseLabel, i + 1, submission.getId());
                
                // Get the run command for this language
                String runCommand = getRunCommand(submission.getLanguage(), fileName);
                
                // Generate unique container name for debugging
                String containerName = "executor-" + submission.getId().substring(0, 8) + "-tc" + (i + 1);

                // Split the Docker command into parts to avoid shell interpretation issues
                List<String> commandParts = new ArrayList<>();
                commandParts.add("docker");
                commandParts.add("run");
                if (!dockerDebugMode) {
                    commandParts.add("--rm");
                }
                commandParts.add("--name");
                commandParts.add(containerName);
                commandParts.add("-i");
                commandParts.add("--cpus=1");
                commandParts.add("--memory=" + testCase.getMemoryLimit() + "m");
                commandParts.add("--memory-swap=" + testCase.getMemoryLimit() + "m");
                // Increased ulimits to prevent "resource temporarily unavailable" errors
                commandParts.add("--ulimit");
                commandParts.add("nofile=256:256");
                commandParts.add("--ulimit");
                commandParts.add("nproc=512:512");
                commandParts.add("--network");
                commandParts.add("none");
                // Run as root to avoid permission issues (security is handled by container isolation)
                commandParts.add("--user");
                commandParts.add("root");
                commandParts.add("-v");
                commandParts.add(workDir.toString() + ":/code");
                commandParts.add("-w");
                commandParts.add("/code");
                commandParts.add(dockerImageName);
                commandParts.add("/bin/bash");
                commandParts.add("-c");
                commandParts.add("timeout " + (testCase.getTimeLimit() / 1000) + " " + runCommand);
                
                ProcessBuilder pb = new ProcessBuilder(commandParts);
                pb.directory(workDir.toFile());

                if (dockerDebugMode) {
                    log.info("Starting container {} for test case {}", containerName, i + 1);
                }

                Process process = pb.start();
                
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(testCase.getInput());
                    writer.flush();
                }
                
                boolean finished = process.waitFor(testCase.getTimeLimit() + 1000, TimeUnit.MILLISECONDS);

                if (!finished) {
                    log.warn("Test case {} timed out for submission {}", i + 1, submission.getId());
                    process.destroyForcibly();
                    // Also kill the Docker container if it's still running
                    try {
                        ProcessBuilder killBuilder = new ProcessBuilder("docker", "kill", containerName);
                        killBuilder.start().waitFor(2, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.debug("Failed to kill container {}: {}", containerName, e.getMessage());
                    }
                    output.append(testCaseLabel).append(i + 1).append(": ").append(ApplicationConstants.RESULT_TIME_LIMIT_EXCEEDED).append("\n");
                    continue;
                }
                
                String result = readOutput(process.getInputStream());
                String error = readOutput(process.getErrorStream());
                
                if (process.exitValue() != 0) {
                    if (error.contains("compilation")) {
                        submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
                        submission.setErrorMessage(error);
                        submission.setScore(0);
                        submission.setTestCasesPassed(0);
                        submission.setTotalTestCases(totalTestCases);
                        submissionRepository.save(submission);
                        return;
                    } else {
                        log.error("Runtime error for test case {}: Exit code={}, Error output: {}",
                                 i + 1, process.exitValue(), error);
                        output.append(testCaseLabel).append(i + 1).append(": ").append(ApplicationConstants.RESULT_RUNTIME_ERROR).append("\n");
                        output.append("  Error: ").append(error.isEmpty() ? "Unknown error (exit code: " + process.exitValue() + ")" : error).append("\n");

                        if (dockerDebugMode) {
                            log.info("Container {} preserved for debugging. Use 'docker logs {}' to see output",
                                    containerName, containerName);
                            output.append("  Debug: Container '").append(containerName).append("' preserved for inspection\n");
                        }
                        continue;
                    }
                }
                
                String expectedOutput = testCase.getExpectedOutput().trim();
                String actualOutput = result.trim();
                
                if (expectedOutput.equals(actualOutput)) {
                    testCasesPassed++;
                    totalScore += pointsPerTestCase;
                    output.append(testCaseLabel).append(i + 1).append(": ").append(ApplicationConstants.RESULT_PASSED).append("\n");
                } else {
                    output.append(testCaseLabel).append(i + 1).append(": ").append(ApplicationConstants.RESULT_FAILED).append("\n");
                    if (!testCase.getIsHidden()) {
                        output.append("  Expected: ").append(expectedOutput).append("\n");
                        output.append("  Got: ").append(actualOutput).append("\n");
                    }
                }
                totalExecutionTime += measureExecutionTime(process);
            }
            
            // Handle remaining points due to integer division
            if (testCasesPassed == totalTestCases && totalTestCases > 0) {
                totalScore = problem.getPoints();
                submission.setStatus(SubmissionStatus.ACCEPTED);
                submission.setOutput("All test cases passed! Score: " + totalScore);
            } else if (testCasesPassed > 0) {
                submission.setStatus(SubmissionStatus.PARTIALLY_ACCEPTED);
                submission.setOutput(output.toString() + "\nTest cases passed: " + testCasesPassed + "/" + totalTestCases + 
                                   "\nScore: " + totalScore + "/" + problem.getPoints());
            } else {
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                submission.setOutput(output.toString() + "\nTest cases passed: 0/" + totalTestCases +
                                   "\nScore: 0/" + problem.getPoints());
            }
            
            submission.setScore(totalScore);
            submission.setTestCasesPassed(testCasesPassed);
            submission.setTotalTestCases(totalTestCases);
            submission.setExecutionTime(totalExecutionTime);
            
        } finally {
            cleanupWorkDir(workDir);
        }
        
        submissionRepository.save(submission);
    }
    
    
    private String getFileName(ProgrammingLanguage language) {
        switch (language) {
            case JAVA: return "Solution.java";
            case PYTHON3: return "solution.py";
            case CPP: return "solution.cpp";
            case C: return "solution.c";
            case JAVASCRIPT: return "solution.js";
            case CSHARP: return "solution.cs";
            case GO: return "solution.go";
            case RUST: return "solution.rs";
            case KOTLIN: return "solution.kt";
            case SWIFT: return "solution.swift";
            default: return "solution.txt";
        }
    }
    
    private String getRunCommand(ProgrammingLanguage language, String fileName) {
        switch (language) {
            case JAVA:
                return "sh -c 'javac " + fileName + " && java Solution'";
            case PYTHON3:
                return "python3 " + fileName;
            case CPP:
                return "sh -c 'g++ -o solution " + fileName + " && ./solution'";
            case C:
                return "sh -c 'gcc -o solution " + fileName + " && ./solution'";
            case JAVASCRIPT:
                return "node " + fileName;
            case CSHARP:
                return "sh -c 'csc " + fileName + " && mono solution.exe'";
            case GO:
                return "go run " + fileName;
            case RUST:
                return "sh -c 'rustc " + fileName + " && ./solution'";
            case KOTLIN:
                return "sh -c 'kotlinc " + fileName + " -include-runtime -d solution.jar && java -jar solution.jar'";
            case SWIFT:
                return "swift " + fileName;
            default:
                throw new UnsupportedOperationException("Language not supported: " + language);
        }
    }
    
    private String readOutput(InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
    
    private long measureExecutionTime(Process process) {
        return 100L;
    }
    
    private void cleanupWorkDir(Path workDir) {
        try {
            Files.walk(workDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("Failed to delete {}: {}", path, e.getMessage());
                    }
                });
        } catch (IOException e) {
            log.error("Failed to cleanup work directory: {}", e.getMessage());
        }
    }
}