package com.shodhacode.dto;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.ProgrammingLanguage;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SubmissionRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotBlank(message = "Code cannot be empty")
    @Size(max = ApplicationConstants.MAX_CODE_LENGTH, message = "Code cannot exceed " + ApplicationConstants.MAX_CODE_LENGTH + " characters")
    private String code;

    @NotNull(message = "Language is required")
    private ProgrammingLanguage language;
}