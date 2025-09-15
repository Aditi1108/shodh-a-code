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
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(length = 10000)
    private String code;

    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language = ProgrammingLanguage.JAVA;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(length = 5000)
    private String output;

    private String errorMessage;
    private LocalDateTime submittedAt = LocalDateTime.now();
    private Long executionTime;
    private Integer score = 0;
    private Integer testCasesPassed = 0;
    private Integer totalTestCases = 0;
}

