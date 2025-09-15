package com.shodhacode.controller;

import com.shodhacode.dto.LeaderboardEntry;
import com.shodhacode.entity.Contest;
import com.shodhacode.entity.SubmissionStatus;
import com.shodhacode.repository.ContestRepository;
import com.shodhacode.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ContestController {

    private final ContestRepository contestRepository;
    private final SubmissionRepository submissionRepository;

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

        List<Object[]> results = submissionRepository.findLeaderboard(
                contestId, SubmissionStatus.ACCEPTED
        );

        List<LeaderboardEntry> leaderboard = results.stream()
                .map(row -> new LeaderboardEntry(
                        (String) row[0],  // username
                        ((Long) row[1]).intValue(),  // problems solved
                        row[2] != null ? ((Long) row[2]).intValue() : 0  // score
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping
    public ResponseEntity<List<Contest>> getAllContests() {
        log.info("Fetching all contests");
        return ResponseEntity.ok(contestRepository.findAll());
    }
}