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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    @Column(length = 1000)
    private String inputFormat;

    @Column(length = 1000)
    private String outputFormat;

    @Column(length = 5000)
    private String sampleInput;

    @Column(length = 5000)
    private String sampleOutput;

    @Column(length = 5000)
    private String testInput;

    @Column(length = 5000)
    private String expectedOutput;

    private Integer points = ApplicationConstants.DEFAULT_PROBLEM_POINTS;
    
    private Integer timeLimit = ApplicationConstants.DEFAULT_TIME_LIMIT;
    
    private Integer memoryLimit = ApplicationConstants.DEFAULT_MEMORY_LIMIT;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<TestCase> testCases = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "contest_id")
    @JsonBackReference
    private Contest contest;
}
