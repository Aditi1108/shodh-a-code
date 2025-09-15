package com.shodhacode.service;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.*;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

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
    
    @Value("${execution.temp.dir:" + ApplicationConstants.DEFAULT_TEMP_DIR + "}")
    private String tempDir = ApplicationConstants.DEFAULT_TEMP_DIR;

    public void executeCode(Submission submission) {
        log.info("Starting code execution for submission {}", submission.getId());
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
        
        try {
            String fileName = getFileName(submission.getLanguage());
            Path codeFile = workDir.resolve(fileName);
            Files.write(codeFile, submission.getCode().getBytes());
            
            List<TestCase> testCases = problem.getTestCases();
            if (testCases.isEmpty() && problem.getTestInput() != null) {
                TestCase defaultTestCase = new TestCase();
                defaultTestCase.setInput(problem.getTestInput());
                defaultTestCase.setExpectedOutput(problem.getExpectedOutput());
                defaultTestCase.setTimeLimit(problem.getTimeLimit() != null ? problem.getTimeLimit() : ApplicationConstants.DEFAULT_TIME_LIMIT);
                defaultTestCase.setMemoryLimit(problem.getMemoryLimit() != null ? problem.getMemoryLimit() : ApplicationConstants.DEFAULT_MEMORY_LIMIT);
                testCases = List.of(defaultTestCase);
            }
            
            int testCasesPassed = 0;
            int totalTestCases = testCases.size();
            int pointsPerTestCase = problem.getPoints() / totalTestCases;
            int totalScore = 0;
            StringBuilder output = new StringBuilder();
            long totalExecutionTime = 0;
            
            for (int i = 0; i < testCases.size(); i++) {
                TestCase testCase = testCases.get(i);
                log.info("Running test case {} for submission {}", i + 1, submission.getId());
                
                String dockerCommand = buildDockerCommand(
                    submission.getLanguage(),
                    workDir.toString(),
                    fileName,
                    testCase.getTimeLimit(),
                    testCase.getMemoryLimit()
                );
                
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", dockerCommand);
                pb.directory(workDir.toFile());
                
                Process process = pb.start();
                
                try (BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(testCase.getInput());
                    writer.flush();
                }
                
                boolean finished = process.waitFor(testCase.getTimeLimit() + 1000, TimeUnit.MILLISECONDS);
                
                if (!finished) {
                    process.destroyForcibly();
                    output.append("Test case ").append(i + 1).append(": Time Limit Exceeded\n");
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
                        output.append("Test case ").append(i + 1).append(": Runtime Error\n");
                        continue;
                    }
                }
                
                String expectedOutput = testCase.getExpectedOutput().trim();
                String actualOutput = result.trim();
                
                if (expectedOutput.equals(actualOutput)) {
                    testCasesPassed++;
                    totalScore += pointsPerTestCase;
                    output.append("Test case ").append(i + 1).append(": Passed\n");
                } else {
                    output.append("Test case ").append(i + 1).append(": Failed\n");
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
    
    private String buildDockerCommand(ProgrammingLanguage language, String workDir, 
                                     String fileName, int timeLimit, int memoryLimit) {
        String runCommand = getRunCommand(language, fileName);
        
        return String.format(
            "docker run --rm -i " +
            "--cpus=\"1\" " +
            "--memory=\"%dm\" " +
            "--memory-swap=\"%dm\" " +
            "--ulimit nofile=64:64 " +
            "--ulimit nproc=32:32 " +
            "--network none " +
            "-v \"%s:/code:ro\" " +
            "-w /code " +
            "--user 1000:1000 " +
            "%s " +
            "timeout %ds %s",
            memoryLimit, memoryLimit,
            workDir,
            dockerImageName,
            timeLimit / 1000,
            runCommand
        );
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