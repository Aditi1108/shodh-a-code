package com.shodhacode.repository;

import com.shodhacode.entity.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {

    Optional<ContestParticipant> findByUserIdAndContestId(Long userId, Long contestId);

    List<ContestParticipant> findByContestId(Long contestId);

    List<ContestParticipant> findByUserId(Long userId);

    boolean existsByUserIdAndContestId(Long userId, Long contestId);
}