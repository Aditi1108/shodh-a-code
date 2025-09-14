package com.shodhacode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

    private Integer points = 100;

    @ManyToOne
    @JoinColumn(name = "contest_id")
    @JsonBackReference
    private Contest contest;
}
