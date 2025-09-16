package com.shodhacode.dto;

import com.shodhacode.entity.Problem;
import com.shodhacode.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDetail {
    private Long id;
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private Integer points;
    private Integer timeLimit;
    private Integer memoryLimit;
    private List<TestCaseSample> sampleTestCases;
    
    public static ProblemDetail from(Problem problem) {
        ProblemDetail detail = new ProblemDetail();
        detail.setId(problem.getId());
        detail.setTitle(problem.getTitle());
        detail.setDescription(problem.getDescription());
        detail.setInputFormat(problem.getInputFormat());
        detail.setOutputFormat(problem.getOutputFormat());
        detail.setConstraints(problem.getConstraints());
        detail.setPoints(problem.getPoints());
        detail.setTimeLimit(problem.getTimeLimit());
        detail.setMemoryLimit(problem.getMemoryLimit());
        
        // Only include non-hidden test cases (sample test cases)
        if (problem.getTestCases() != null) {
            detail.setSampleTestCases(
                problem.getTestCases().stream()
                    .filter(tc -> !tc.getIsHidden())
                    .map(TestCaseSample::from)
                    .collect(Collectors.toList())
            );
        }
        
        return detail;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseSample {
        private String input;
        private String expectedOutput;
        
        public static TestCaseSample from(TestCase testCase) {
            return new TestCaseSample(
                testCase.getInput(),
                testCase.getExpectedOutput()
            );
        }
    }
}