package com.shodhacode.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "contestParticipations")
@ToString(exclude = "contestParticipations")
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
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ContestParticipant> contestParticipations = new HashSet<>();
}