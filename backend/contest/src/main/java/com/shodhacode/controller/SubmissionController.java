package com.shodhacode.controller;

import com.shodhacode.dto.SubmissionRequest;
import com.shodhacode.dto.SubmissionResponse;
import com.shodhacode.entity.Submission;
import com.shodhacode.entity.Problem;
import com.shodhacode.entity.User;
import com.shodhacode.repository.ProblemRepository;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.repository.UserRepository;
import com.shodhacode.service.SimpleQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class SubmissionController {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SimpleQueueService queueService;

    @PostMapping
    public ResponseEntity<?> submitCode(@Valid @RequestBody SubmissionRequest request) {
        log.info("Received submission from user {} for problem {}",
                request.getUserId(), request.getProblemId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate problem exists
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // Create submission
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());

        submission = submissionRepository.save(submission);
        log.info("Created submission with ID: {}", submission.getId());

        // Add to processing queue
        queueService.addToQueue(submission.getId());

        return ResponseEntity.ok(new SubmissionResponse(
                submission.getId(),
                submission.getStatus().toString()
        ));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<Submission> getSubmission(@PathVariable String submissionId) {
        log.debug("Fetching submission: {}", submissionId);
        return submissionRepository.findById(submissionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}