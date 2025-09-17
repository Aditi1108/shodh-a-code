package com.shodhacode.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "contest_participants", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contest_id"}))
@Data
public class ContestParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
    
    @Column(name = "score")
    private Integer score = 0;
    
    @Column(name = "problems_solved")
    private Integer problemsSolved = 0;
}