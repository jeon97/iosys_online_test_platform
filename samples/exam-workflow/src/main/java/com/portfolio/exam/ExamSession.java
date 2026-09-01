package com.portfolio.exam;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ExamSession {
    private final Instant opensAt;
    private final Instant closesAt;
    private final Clock clock;
    private final Map<String, AnswerSubmission> answers = new HashMap<>();
    private final Set<String> processedSubmissionIds = new HashSet<>();
    private ExamStatus status = ExamStatus.READY;

    public ExamSession(Instant opensAt, Instant closesAt, Clock clock) {
        if (opensAt == null || closesAt == null || !opensAt.isBefore(closesAt)) {
            throw new IllegalArgumentException("valid exam period is required");
        }
        this.opensAt = opensAt;
        this.closesAt = closesAt;
        this.clock = clock;
    }

    public void start() {
        Instant now = clock.instant();
        if (status != ExamStatus.READY) {
            throw new IllegalStateException("only a ready exam can start");
        }
        if (now.isBefore(opensAt) || !now.isBefore(closesAt)) {
            throw new IllegalStateException("exam is outside its allowed period");
        }
        status = ExamStatus.IN_PROGRESS;
    }

    public SubmissionResult submit(AnswerSubmission submission) {
        if (status != ExamStatus.IN_PROGRESS || !clock.instant().isBefore(closesAt)) {
            throw new IllegalStateException("answers are accepted only during the exam");
        }
        if (!processedSubmissionIds.add(submission.submissionId())) {
            return SubmissionResult.DUPLICATE;
        }
        answers.put(submission.questionId(), submission);
        return SubmissionResult.SAVED;
    }

    public void finish() {
        if (status != ExamStatus.IN_PROGRESS) {
            throw new IllegalStateException("only an active exam can finish");
        }
        status = ExamStatus.FINISHED;
    }

    public ExamStatus status() {
        return status;
    }

    public Map<String, AnswerSubmission> answers() {
        return Map.copyOf(answers);
    }

    public enum SubmissionResult {
        SAVED,
        DUPLICATE
    }
}

