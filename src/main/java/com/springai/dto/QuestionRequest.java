package com.springai.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequest(
		@NotBlank(message = "questionText must not be blank") String questionText,
		@NotBlank(message = "converSationId must not be blank") String converSationId
) {
}
