package com.shodhacode.entity;

import com.shodhacode.constants.ApplicationConstants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "test_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Problem relationship
    @ManyToOne
    @JoinColumn(name = "problem_id")
    @JsonBackReference
    private Problem problem;
    
    // Test case visibility
    private Boolean isHidden = false;
    
    // Constraints (can override problem defaults)
    private Integer timeLimit = ApplicationConstants.DEFAULT_TIME_LIMIT;
    
    private Integer memoryLimit = ApplicationConstants.DEFAULT_MEMORY_LIMIT;
    
    // Test data (large text fields at the end)
    @Column(columnDefinition = "TEXT")
    private String input;
    
    @Column(columnDefinition = "TEXT")
    private String expectedOutput;
}