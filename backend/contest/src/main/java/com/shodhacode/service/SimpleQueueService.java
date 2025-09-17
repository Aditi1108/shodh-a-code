package com.shodhacode.service;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.repository.SubmissionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleQueueService {

    private final Queue<String> submissionQueue = new ConcurrentLinkedQueue<>();
    private final SubmissionRepository submissionRepository;
    private final CodeExecutorService codeExecutorService;

    // Thread pool for parallel submission processing
    private ExecutorService executorService;
    private volatile boolean running = true;

    @PostConstruct
    public void startProcessing() {
        // Create thread pool with 4 worker threads for parallel execution
        executorService = Executors.newFixedThreadPool(4);

        // Main queue processor thread
        Thread processor = new Thread(() -> {
            while (running) {
                try {
                    String submissionId = submissionQueue.poll();
                    if (submissionId != null) {
                        // Submit to thread pool for async execution
                        executorService.submit(() -> {
                            try {
                                processSubmission(submissionId);
                            } catch (Exception e) {
                                log.error("Error processing submission {}: {}", submissionId, e.getMessage(), e);
                            }
                        });
                    } else {
                        Thread.sleep(ApplicationConstants.QUEUE_PROCESSING_DELAY);
                    }
                } catch (Exception e) {
                    log.error("Error in queue processor", e);
                }
            }
        });
        processor.setDaemon(true);
        processor.start();
        log.info("Queue processor started with 4 worker threads");
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        if (executorService != null) {
            executorService.shutdown();
            log.info("Queue processor shut down");
        }
    }

    public void addToQueue(String submissionId) {
        submissionQueue.offer(submissionId);
        log.info("Added submission {} to queue. Queue size: {}",
                submissionId, submissionQueue.size());
    }

    private void processSubmission(String submissionId) {
        submissionRepository.findById(submissionId).ifPresent(submission -> {
            log.info("Processing submission: {}", submissionId);
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);
            codeExecutorService.executeCode(submission);
        });
    }
}