package com.shodhacode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private int rank;
    private Long userId;
    private String username;
    private String fullName;
    private Integer score;
    private Integer problemsSolved;
    private String lastSubmission;
}