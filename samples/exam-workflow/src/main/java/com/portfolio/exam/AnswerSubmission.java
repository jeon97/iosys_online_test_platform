package com.portfolio.exam;

import java.time.Instant;

public record AnswerSubmission(
        String submissionId,
        String questionId,
        String answer,
        Instant submittedAt
) {
    public AnswerSubmission {
        if (submissionId == null || submissionId.isBlank()) {
            throw new IllegalArgumentException("submissionId is required");
        }
        if (questionId == null || questionId.isBlank()) {
            throw new IllegalArgumentException("questionId is required");
        }
        if (submittedAt == null) {
            throw new IllegalArgumentException("submittedAt is required");
        }
    }
}

