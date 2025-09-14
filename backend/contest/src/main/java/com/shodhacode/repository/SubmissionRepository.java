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

    @Query("SELECT s.user.username as username, " +
            "COUNT(DISTINCT s.problem.id) as problemsSolved, " +
            "SUM(s.problem.points) as score " +
            "FROM Submission s " +
            "WHERE s.status = :status " +
            "AND s.problem.contest.id = :contestId " +
            "GROUP BY s.user.username " +
            "ORDER BY score DESC")
    List<Object[]> findLeaderboard(@Param("contestId") Long contestId,
                                   @Param("status") SubmissionStatus status);
}