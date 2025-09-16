package com.shodhacode.entity;

import com.shodhacode.constants.ApplicationConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core problem information
    private String title;
    
    // Contest relationship
    @ManyToOne
    @JoinColumn(name = "contest_id")
    @JsonBackReference
    private Contest contest;
    
    // Problem statement fields
    @Column(length = 5000)
    private String description;

    @Column(length = 1000)
    private String inputFormat;

    @Column(length = 1000)
    private String outputFormat;
    
    // Constraints text (e.g., "1 ≤ n ≤ 100", "Array length ≤ 10^5")
    @Column(length = 2000)
    private String constraints;
    
    // Scoring and execution limits
    private Integer points = ApplicationConstants.DEFAULT_PROBLEM_POINTS;
    
    private Integer timeLimit = ApplicationConstants.DEFAULT_TIME_LIMIT;
    
    private Integer memoryLimit = ApplicationConstants.DEFAULT_MEMORY_LIMIT;

    // All test cases (both visible samples and hidden tests)
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TestCase> testCases = new ArrayList<>();
}
