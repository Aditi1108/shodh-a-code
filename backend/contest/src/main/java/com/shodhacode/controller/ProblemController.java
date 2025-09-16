package com.shodhacode.controller;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.dto.ProblemDetail;
import com.shodhacode.dto.ProblemSummary;
import com.shodhacode.entity.Problem;
import com.shodhacode.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApplicationConstants.PROBLEMS_PATH)
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemRepository problemRepository;

    @GetMapping
    public ResponseEntity<List<ProblemSummary>> getAllProblems() {
        log.info("Fetching all problems");
        List<Problem> problems = problemRepository.findAll();
        List<ProblemSummary> summaries = problems.stream()
                .map(ProblemSummary::from)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDetail> getProblemById(@PathVariable Long id) {
        log.info("Fetching problem with ID: {}", id);
        return problemRepository.findById(id)
                .map(problem -> ResponseEntity.ok(ProblemDetail.from(problem)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/contest/{contestId}")
    public ResponseEntity<List<ProblemSummary>> getProblemsByContest(@PathVariable Long contestId) {
        log.info("Fetching problems for contest ID: {}", contestId);
        List<Problem> problems = problemRepository.findByContestId(contestId);
        List<ProblemSummary> summaries = problems.stream()
                .map(ProblemSummary::from)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(summaries);
    }
}