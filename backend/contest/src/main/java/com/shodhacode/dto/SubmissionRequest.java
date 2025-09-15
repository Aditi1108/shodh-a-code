package com.shodhacode.dto;

import com.shodhacode.constants.ApplicationConstants;
import com.shodhacode.entity.ProgrammingLanguage;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SubmissionRequest {
    @NotNull(message = ApplicationConstants.USER_ID_REQUIRED)
    private Long userId;

    @NotNull(message = ApplicationConstants.PROBLEM_ID_REQUIRED)
    private Long problemId;

    @NotBlank(message = ApplicationConstants.CODE_EMPTY)
    @Size(max = ApplicationConstants.MAX_CODE_LENGTH, message = ApplicationConstants.CODE_TOO_LONG)
    private String code;

    @NotNull(message = ApplicationConstants.LANGUAGE_REQUIRED)
    private ProgrammingLanguage language;
}