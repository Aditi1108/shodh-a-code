package com.shodhacode.controller;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.dto.SubmissionRequest;
import com.shodhacode.dto.SubmissionResponse;
import com.shodhacode.entity.Contest;
import com.shodhacode.entity.Submission;
import com.shodhacode.entity.Problem;
import com.shodhacode.entity.User;
import com.shodhacode.entity.ProgrammingLanguage;
import com.shodhacode.repository.ContestParticipantRepository;
import com.shodhacode.repository.ProblemRepository;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.repository.UserRepository;
import com.shodhacode.service.SimpleQueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApplicationConstants.SUBMISSIONS_PATH)
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final ContestParticipantRepository contestParticipantRepository;
    private final SimpleQueueService queueService;

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@Valid @RequestBody SubmissionRequest request) {
        log.info("Received RUN request from user {} for problem {}",
                request.getUserId(), request.getProblemId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate problem exists
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // Check if user has joined the contest
        Contest contest = problem.getContest();
        if (contest != null) {
            // Check if contest has ended
            LocalDateTime now = LocalDateTime.now();
            if (contest.getEndTime() != null && now.isAfter(contest.getEndTime())) {
                log.warn("User {} attempting to submit to ended contest {} - rejecting submission", user.getUsername(), contest.getTitle());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Contest has ended. You cannot attempt problems from ended contests.");
            }

            boolean hasJoined = contestParticipantRepository.existsByUserIdAndContestId(user.getId(), contest.getId());
            if (!hasJoined) {
                log.warn("User {} has not joined contest {} - rejecting submission", user.getUsername(), contest.getTitle());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please join the contest first before attempting problems");
            }
        }

        // Create submission for testing (not saved to leaderboard)
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setIsTestRun(true);  // Mark as test run

        submission = submissionRepository.save(submission);
        log.info("Created test run with ID: {}", submission.getId());

        // Add to processing queue (will only run sample test cases)
        queueService.addToQueue(submission.getId());

        return ResponseEntity.ok(new SubmissionResponse(
                submission.getId(),
                submission.getStatus().toString()
        ));
    }

    @PostMapping
    public ResponseEntity<?> submitCode(@Valid @RequestBody SubmissionRequest request) {
        log.info("Received SUBMIT from user {} for problem {}",
                request.getUserId(), request.getProblemId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate problem exists
        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // Check if user has joined the contest
        Contest contest = problem.getContest();
        if (contest != null) {
            // Check if contest has ended
            LocalDateTime now = LocalDateTime.now();
            if (contest.getEndTime() != null && now.isAfter(contest.getEndTime())) {
                log.warn("User {} attempting to submit to ended contest {} - rejecting submission", user.getUsername(), contest.getTitle());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Contest has ended. You cannot attempt problems from ended contests.");
            }

            boolean hasJoined = contestParticipantRepository.existsByUserIdAndContestId(user.getId(), contest.getId());
            if (!hasJoined) {
                log.warn("User {} has not joined contest {} - rejecting submission", user.getUsername(), contest.getTitle());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Please join the contest first before attempting problems");
            }
        }

        // Create submission
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setIsTestRun(false);  // Full submission

        submission = submissionRepository.save(submission);
        log.info("Created submission with ID: {}", submission.getId());

        // Add to processing queue (will run ALL test cases)
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
    
    @GetMapping("/user/{userId}/problem/{problemId}/latest")
    public ResponseEntity<Submission> getLatestSubmission(@PathVariable Long userId, 
                                                          @PathVariable Long problemId) {
        log.info("Fetching latest submission for user {} on problem {}", userId, problemId);
        return submissionRepository.findFirstByUserIdAndProblemIdOrderBySubmittedAtDesc(userId, problemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}/problem/{problemId}")
    public ResponseEntity<List<Submission>> getUserProblemSubmissions(@PathVariable Long userId,
                                                                      @PathVariable Long problemId) {
        log.info("Fetching all submissions for user {} on problem {}", userId, problemId);
        List<Submission> submissions = submissionRepository.findByUserIdAndProblemId(userId, problemId);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/user/{userId}/contest/{contestId}")
    public ResponseEntity<List<Submission>> getUserContestSubmissions(@PathVariable Long userId,
                                                                      @PathVariable Long contestId) {
        log.info("Fetching all submissions for user {} in contest {}", userId, contestId);
        List<Submission> submissions = submissionRepository.findByUserIdAndContestId(userId, contestId);
        return ResponseEntity.ok(submissions);
    }
    
    @GetMapping("/languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        log.info("Fetching supported programming languages");
        List<String> languages = Arrays.stream(ProgrammingLanguage.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(languages);
    }
}