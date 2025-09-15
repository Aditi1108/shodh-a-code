package com.shodhacode.dto;

import com.shodhacode.entity.Contest;
import com.shodhacode.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinContestResponse {
    private Long userId;
    private String username;
    private Long contestId;
    private String contestTitle;
    private String message;
    
    public static JoinContestResponse from(User user, Contest contest) {
        return JoinContestResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .contestId(contest.getId())
                .contestTitle(contest.getTitle())
                .message("Successfully joined contest!")
                .build();
    }
}