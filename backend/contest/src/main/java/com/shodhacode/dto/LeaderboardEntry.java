package com.shodhacode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardEntry {
    private String username;
    private Integer problemsSolved;
    private Integer score;
}