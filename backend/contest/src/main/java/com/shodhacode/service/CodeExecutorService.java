package com.shodhacode.service;

import com.shodhacode.entity.Submission;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutorService {

    private final SubmissionRepository submissionRepository;
    private final Random random = new Random();

    public void executeCode(Submission submission) {
        try {
            // Simulate execution time (2-5 seconds)
            Thread.sleep(2000 + random.nextInt(3000));

            // Mock execution logic - 70% success rate
            if (random.nextDouble() < 0.7) {
                submission.setStatus(SubmissionStatus.ACCEPTED);
                submission.setOutput("Test cases passed!");
                submission.setExecutionTime(150L + random.nextInt(100));
                log.info("Submission {} ACCEPTED", submission.getId());
            } else {
                // Randomly choose a failure type
                int failType = random.nextInt(3);
                switch (failType) {
                    case 0:
                        submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                        submission.setOutput("Expected: 30, Got: 31");
                        break;
                    case 1:
                        submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
                        submission.setErrorMessage("Syntax error on line 5");
                        break;
                    case 2:
                        submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                        submission.setErrorMessage("Time limit exceeded");
                        break;
                }
                submission.setExecutionTime(200L + random.nextInt(300));
                log.info("Submission {} failed with status: {}",
                        submission.getId(), submission.getStatus());
            }

        } catch (InterruptedException e) {
            log.error("Execution interrupted", e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Internal error occurred");
        }

        submissionRepository.save(submission);
    }
}