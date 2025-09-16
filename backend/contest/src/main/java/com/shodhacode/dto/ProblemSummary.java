package com.shodhacode.dto;

import com.shodhacode.entity.Problem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSummary {
    private Long id;
    private String title;
    private Integer points;
    
    public static ProblemSummary from(Problem problem) {
        return new ProblemSummary(
            problem.getId(),
            problem.getTitle(),
            problem.getPoints()
        );
    }
}