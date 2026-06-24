package com.springai.dto;

import java.time.Instant;

public record QuestionAnswerPair(
        String question,
        String answer,
        Instant timestamp
) {
}

