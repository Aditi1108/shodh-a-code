package com.shodhacode.controller;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.dto.ContestSummary;
import com.shodhacode.dto.ContestWithProblems;
import com.shodhacode.dto.JoinContestRequest;
import com.shodhacode.dto.JoinContestResponse;
import com.shodhacode.dto.LeaderboardEntry;
import com.shodhacode.dto.ProblemSummary;
import com.shodhacode.dto.ProblemDetail;
import com.shodhacode.entity.Contest;
import com.shodhacode.entity.ContestParticipant;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.entity.User;
import com.shodhacode.repository.ContestRepository;
import com.shodhacode.repository.ContestParticipantRepository;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApplicationConstants.CONTESTS_PATH)
@RequiredArgsConstructor
@Slf4j
public class ContestController {

    private final ContestRepository contestRepository;
    private final SubmissionRepository submissionRepository;
    private final ContestParticipantRepository contestParticipantRepository;
    private final UserService userService;

    @GetMapping("/{contestId}")
    public ResponseEntity<ContestWithProblems> getContest(@PathVariable Long contestId) {
        log.info("Fetching contest with ID: {}", contestId);
        return contestRepository.findById(contestId)
                .map(contest -> ResponseEntity.ok(ContestWithProblems.from(contest)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(@PathVariable Long contestId) {
        log.info("Fetching leaderboard for contest: {}", contestId);

        // Get contest with participants
        Optional<Contest> contestOpt = contestRepository.findById(contestId);
        if (contestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Contest contest = contestOpt.get();

        // Get all contest participants and their scores
        List<ContestParticipant> participants = contestParticipantRepository.findByContestId(contestId);
        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        for (ContestParticipant participant : participants) {
            // Include all participants who have joined the contest
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setUserId(participant.getUser().getId());
            entry.setUsername(participant.getUser().getUsername());
            entry.setFullName(participant.getUser().getFullName());
            entry.setScore(participant.getScore() != null ? participant.getScore() : 0);
            entry.setProblemsSolved(participant.getProblemsSolved() != null ? participant.getProblemsSolved() : 0);

            // Get last submission time
            submissionRepository.findByUserIdAndContestId(
                participant.getUser().getId(), contestId
            ).stream()
                .filter(s -> !s.getIsTestRun()) // Only count real submissions, not test runs
                .map(s -> s.getSubmittedAt())
                .max(LocalDateTime::compareTo)
                .ifPresent(lastSubmission -> entry.setLastSubmission(lastSubmission.toString()));

            entry.setRank(0); // Will be set after sorting
            leaderboard.add(entry);
        }

        // Sort by score descending, then problems solved descending
        leaderboard.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getScore(), a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(b.getProblemsSolved(), a.getProblemsSolved());
        });

        // Assign ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping
    public ResponseEntity<List<ContestWithProblems>> getAllContests() {
        log.info("Fetching all contests");
        List<Contest> contests = contestRepository.findAll();
        List<ContestWithProblems> contestDtos = contests.stream()
                .map(ContestWithProblems::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(contestDtos);
    }
    
    @GetMapping("/{contestId}/problems")
    public ResponseEntity<List<ProblemSummary>> getContestProblems(@PathVariable Long contestId) {
        log.info("Fetching problems for contest: {}", contestId);
        return contestRepository.findById(contestId)
                .map(contest -> {
                    List<ProblemSummary> summaries = contest.getProblems().stream()
                            .map(ProblemSummary::from)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(summaries);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/summary")
    public ResponseEntity<List<ContestSummary>> getAllContestSummaries() {
        log.info("Fetching all contest summaries with user counts");
        
        List<Contest> contests = contestRepository.findAll();
        List<ContestSummary> summaries = contests.stream()
                .map(contest -> {
                    // Use actual participant count from contest participants
                    Long userCount = (long) contest.getContestParticipants().size();
                    return ContestSummary.from(contest, userCount);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(summaries);
    }
    
    @GetMapping("/{contestId}/participants/{userId}")
    public ResponseEntity<Boolean> hasUserJoined(@PathVariable Long contestId, @PathVariable Long userId) {
        log.info("Checking if user {} has joined contest {}", userId, contestId);
        boolean hasJoined = contestParticipantRepository.existsByUserIdAndContestId(userId, contestId);
        return ResponseEntity.ok(hasJoined);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinContest(@Valid @RequestBody JoinContestRequest request) {
        log.info("User {} attempting to join contest {}", request.getUsername(), request.getContestId());
        
        // Check if contest exists
        Optional<Contest> contestOpt = contestRepository.findById(request.getContestId());
        if (contestOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Contest not found with ID: " + request.getContestId());
        }
        
        // Check if user exists
        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found. Please register first.");
        }
        
        Contest contest = contestOpt.get();
        User user = userOpt.get();
        
        // Check if contest is active
        if (!contest.getIsActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Contest is not active");
        }

        // Check if user is already participating
        if (contestParticipantRepository.existsByUserIdAndContestId(user.getId(), contest.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User is already participating in this contest");
        }

        // Create new contest participant
        ContestParticipant participant = new ContestParticipant();
        participant.setUser(user);
        participant.setContest(contest);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setScore(0);
        participant.setProblemsSolved(0);

        contestParticipantRepository.save(participant);

        JoinContestResponse response = JoinContestResponse.from(user, contest);
        log.info("User {} successfully joined contest {}", user.getUsername(), contest.getTitle());

        return ResponseEntity.ok(response);
    }
}