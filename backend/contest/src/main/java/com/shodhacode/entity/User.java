package com.shodhacode.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "participatingContests")
@ToString(exclude = "participatingContests")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(unique = true)
    private String email;

    private Integer score = 0;
    private Integer problemsSolved = 0;
    
    @ManyToMany
    @JoinTable(
        name = "contest_participants",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "contest_id")
    )
    private Set<Contest> participatingContests = new HashSet<>();
}