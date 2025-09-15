package com.shodhacode.repository;

import com.shodhacode.entity.Submission;
import com.shodhacode.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {

    List<Submission> findByUserIdAndProblemId(Long userId, Long problemId);
    
    Optional<Submission> findFirstByUserIdAndProblemIdOrderBySubmittedAtDesc(Long userId, Long problemId);
    
    @Query("SELECT s FROM Submission s " +
           "WHERE s.user.id = :userId " +
           "AND s.problem.contest.id = :contestId " +
           "ORDER BY s.submittedAt DESC")
    List<Submission> findByUserIdAndContestId(@Param("userId") Long userId, 
                                              @Param("contestId") Long contestId);

    @Query("SELECT s.user.username as username, " +
            "COUNT(DISTINCT CASE WHEN s.score > 0 THEN s.problem.id END) as problemsSolved, " +
            "SUM(s.score) as totalScore " +
            "FROM Submission s " +
            "WHERE s.problem.contest.id = :contestId " +
            "AND s.id IN (SELECT MAX(s2.id) FROM Submission s2 " +
            "             WHERE s2.user.id = s.user.id " +
            "             AND s2.problem.id = s.problem.id " +
            "             GROUP BY s2.user.id, s2.problem.id) " +
            "GROUP BY s.user.username " +
            "ORDER BY totalScore DESC, problemsSolved DESC")
    List<Object[]> findLeaderboard(@Param("contestId") Long contestId);
    
    @Query("SELECT COUNT(DISTINCT s.user.id) " +
            "FROM Submission s " +
            "WHERE s.problem.contest.id = :contestId")
    Long countUniqueUsersByContestId(@Param("contestId") Long contestId);
}