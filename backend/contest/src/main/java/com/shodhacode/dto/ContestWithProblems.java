package com.shodhacode.dto;

import com.shodhacode.entity.Contest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContestWithProblems {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isActive;
    private List<ProblemSummary> problems;
    
    public static ContestWithProblems from(Contest contest) {
        ContestWithProblems dto = new ContestWithProblems();
        dto.setId(contest.getId());
        dto.setTitle(contest.getTitle());
        dto.setDescription(contest.getDescription());
        dto.setStartTime(contest.getStartTime());
        dto.setEndTime(contest.getEndTime());
        dto.setIsActive(contest.getIsActive());
        
        // Only include problem summaries (id, title, points)
        if (contest.getProblems() != null) {
            dto.setProblems(
                contest.getProblems().stream()
                    .map(ProblemSummary::from)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }
}