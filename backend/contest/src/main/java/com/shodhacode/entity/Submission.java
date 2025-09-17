package com.shodhacode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Foreign Keys - Important relationships
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    // Core submission data
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language = ProgrammingLanguage.JAVA;
    
    // Scoring information
    private Integer score = 0;
    private Integer testCasesPassed = 0;
    private Integer totalTestCases = 0;
    
    // Timestamps
    private LocalDateTime submittedAt = LocalDateTime.now();

    // Performance metrics
    private Long executionTime;

    // Test run flag (true = only run sample cases, false = full submission)
    private Boolean isTestRun = false;
    
    // Large text fields at the end
    @Column(length = 10000)
    private String code;
    
    @Column(length = 5000)
    private String output;
    
    private String errorMessage;
}

