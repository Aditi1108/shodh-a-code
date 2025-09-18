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
        log.info("==================================================");
        log.info("Starting SimpleQueueService");
        log.info("Creating thread pool with 4 worker threads");

        // Create thread pool with 4 worker threads for parallel execution
        executorService = Executors.newFixedThreadPool(4);

        // Main queue processor thread
        Thread processor = new Thread(() -> {
            log.info("Queue processor thread started");
            while (running) {
                try {
                    String submissionId = submissionQueue.poll();
                    if (submissionId != null) {
                        log.debug("Dequeued submission {} for processing", submissionId);
                        // Submit to thread pool for async execution
                        executorService.submit(() -> {
                            log.debug("Worker thread picked up submission {}", submissionId);
                            try {
                                processSubmission(submissionId);
                            } catch (Exception e) {
                                log.error("Error processing submission {}: {}", submissionId, e.getMessage(), e);
                            }
                        });
                    } else {
                        // Queue is empty, wait before checking again
                        Thread.sleep(ApplicationConstants.QUEUE_PROCESSING_DELAY);
                    }
                } catch (InterruptedException e) {
                    log.info("Queue processor interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Unexpected error in queue processor", e);
                }
            }
            log.info("Queue processor thread stopped");
        });
        processor.setDaemon(true);
        processor.start();
        log.info("✓ Queue processor started successfully with 4 worker threads");
        log.info("==================================================");
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SimpleQueueService...");
        running = false;

        if (executorService != null) {
            executorService.shutdown();
            log.info("Executor service shutdown initiated");

            try {
                if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate in 5 seconds, forcing shutdown");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for executor shutdown");
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("Queue size at shutdown: {} submissions pending", submissionQueue.size());
        log.info("SimpleQueueService shut down complete");
    }

    public void addToQueue(String submissionId) {
        boolean added = submissionQueue.offer(submissionId);
        if (added) {
            log.info("✓ Added submission {} to queue. Current queue size: {}",
                    submissionId.substring(0, Math.min(8, submissionId.length())),
                    submissionQueue.size());
        } else {
            log.error("✗ Failed to add submission {} to queue", submissionId);
        }
    }

    private void processSubmission(String submissionId) {
        log.info("Starting to process submission {}", submissionId.substring(0, Math.min(8, submissionId.length())));

        submissionRepository.findById(submissionId).ifPresentOrElse(
            submission -> {
                log.info("Found submission {} - User: {}, Problem: {}, Language: {}",
                        submission.getId().substring(0, Math.min(8, submission.getId().length())),
                        submission.getUser() != null ? submission.getUser().getUsername() : "unknown",
                        submission.getProblem() != null ? submission.getProblem().getTitle() : "unknown",
                        submission.getLanguage());

                // Update status to RUNNING
                submission.setStatus(SubmissionStatus.RUNNING);
                submissionRepository.save(submission);
                log.debug("Updated submission {} status to RUNNING", submissionId.substring(0, Math.min(8, submissionId.length())));

                // Execute the code
                codeExecutorService.executeCode(submission);

                log.info("✓ Completed processing submission {}",
                        submissionId.substring(0, Math.min(8, submissionId.length())));
            },
            () -> {
                log.error("✗ Submission {} not found in database", submissionId);
            }
        );
    }
}