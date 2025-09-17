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
        
        // Get submission results
        List<Object[]> submissionResults = submissionRepository.findLeaderboard(contestId);
        Map<String, LeaderboardEntry> leaderboardMap = new HashMap<>();
        
        // Add submission results to map
        for (Object[] row : submissionResults) {
            String username = (String) row[0];
            int problemsSolved = ((Long) row[1]).intValue();
            int totalScore = row[2] != null ? ((Long) row[2]).intValue() : 0;
            leaderboardMap.put(username, new LeaderboardEntry(username, problemsSolved, totalScore));
        }
        
        // Add all participants (including those with 0 score)
        for (User participant : contest.getParticipants()) {
            if (!leaderboardMap.containsKey(participant.getUsername())) {
                leaderboardMap.put(participant.getUsername(), 
                    new LeaderboardEntry(participant.getUsername(), 0, 0));
            }
        }
        
        // Sort by score descending, then problems solved descending
        List<LeaderboardEntry> leaderboard = new ArrayList<>(leaderboardMap.values());
        leaderboard.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getScore(), a.getScore());
            if (scoreCompare != 0) return scoreCompare;
            return Integer.compare(b.getProblemsSolved(), a.getProblemsSolved());
        });

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
                    // Use actual participant count from the many-to-many relationship
                    Long userCount = (long) contest.getParticipants().size();
                    return ContestSummary.from(contest, userCount);
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
        
        // Add user to contest participants
        user.getParticipatingContests().add(contest);
        contest.getParticipants().add(user);
        
        // Save the relationship
        userService.save(user);
        
        JoinContestResponse response = JoinContestResponse.from(user, contest);
        log.info("User {} successfully joined contest {}", user.getUsername(), contest.getTitle());
        
        return ResponseEntity.ok(response);
    }
}