package com.shodhacode.controller;

import com.shodhacode.dto.ContestSummary;
import com.shodhacode.dto.JoinContestRequest;
import com.shodhacode.dto.JoinContestResponse;
import com.shodhacode.dto.LeaderboardEntry;
import com.shodhacode.entity.Contest;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.entity.User;
import com.shodhacode.repository.ContestRepository;
import com.shodhacode.repository.SubmissionRepository;
import com.shodhacode.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ContestController {

    private final ContestRepository contestRepository;
    private final SubmissionRepository submissionRepository;
    private final UserService userService;

    @GetMapping("/{contestId}")
    public ResponseEntity<Contest> getContest(@PathVariable Long contestId) {
        log.info("Fetching contest with ID: {}", contestId);
        return contestRepository.findById(contestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(@PathVariable Long contestId) {
        log.info("Fetching leaderboard for contest: {}", contestId);

        List<Object[]> results = submissionRepository.findLeaderboard(contestId);

        List<LeaderboardEntry> leaderboard = results.stream()
                .map(row -> new LeaderboardEntry(
                        (String) row[0],  // username
                        ((Long) row[1]).intValue(),  // problems solved (with score > 0)
                        row[2] != null ? ((Long) row[2]).intValue() : 0  // total score
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping
    public ResponseEntity<List<Contest>> getAllContests() {
        log.info("Fetching all contests");
        return ResponseEntity.ok(contestRepository.findAll());
    }
    
    @GetMapping("/summary")
    public ResponseEntity<List<ContestSummary>> getAllContestSummaries() {
        log.info("Fetching all contest summaries with user counts");
        
        List<Contest> contests = contestRepository.findAll();
        List<ContestSummary> summaries = contests.stream()
                .map(contest -> {
                    Long userCount = submissionRepository.countUniqueUsersByContestId(contest.getId());
                    return ContestSummary.from(contest, userCount != null ? userCount : 0L);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(summaries);
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
        
        JoinContestResponse response = JoinContestResponse.from(user, contest);
        log.info("User {} successfully joined contest {}", user.getUsername(), contest.getTitle());
        
        return ResponseEntity.ok(response);
    }
}