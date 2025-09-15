package com.shodhacode.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class JoinContestRequest {
    @NotNull(message = "Contest ID is required")
    private Long contestId;
    
    @NotBlank(message = "Username is required")
    private String username;
}