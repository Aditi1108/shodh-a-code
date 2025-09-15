package com.shodhacode.service;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.Submission;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.repository.SubmissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleQueueService {

    private final Queue<String> submissionQueue = new ConcurrentLinkedQueue<>();
    private final SubmissionRepository submissionRepository;
    private final CodeExecutorService codeExecutorService;

    @PostConstruct
    public void startProcessing() {
        Thread processor = new Thread(() -> {
            while (true) {
                try {
                    String submissionId = submissionQueue.poll();
                    if (submissionId != null) {
                        processSubmission(submissionId);
                    } else {
                        Thread.sleep(ApplicationConstants.QUEUE_PROCESSING_DELAY);
                    }
                } catch (Exception e) {
                    log.error("Error processing submission", e);
                }
            }
        });
        processor.setDaemon(true);
        processor.start();
        log.info("Queue processor started successfully");
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