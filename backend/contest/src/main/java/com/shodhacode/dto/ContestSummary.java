package com.shodhacode.dto;

import com.shodhacode.entity.Contest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContestSummary {
    private Long id;
    private String title;
    private String description;
    private Boolean isActive;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer problemCount;
    private Long registeredUsers;
    
    public static ContestSummary from(Contest contest, Long userCount) {
        return ContestSummary.builder()
                .id(contest.getId())
                .title(contest.getTitle())
                .description(contest.getDescription())
                .isActive(contest.getIsActive())
                .startTime(contest.getStartTime())
                .endTime(contest.getEndTime())
                .problemCount(contest.getProblems() != null ? contest.getProblems().size() : 0)
                .registeredUsers(userCount)
                .build();
    }
}