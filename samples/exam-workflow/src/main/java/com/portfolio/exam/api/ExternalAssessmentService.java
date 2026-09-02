package com.portfolio.exam.api;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ExternalAssessmentService {
    private final ExamCatalog catalog;
    private final WrongAnswerRepository wrongAnswers;
    private final Set<String> processedRequests = new HashSet<>();

    public ExternalAssessmentService(ExamCatalog catalog, WrongAnswerRepository wrongAnswers) {
        this.catalog = Objects.requireNonNull(catalog);
        this.wrongAnswers = Objects.requireNonNull(wrongAnswers);
    }

    public List<ExamSummary> availableExams(Identity identity, String requestedUserId, String courseCode) {
        verifyOwner(identity, requestedUserId);
        return catalog.findOpenExams(courseCode).stream()
                .filter(exam -> exam.allowedUserIds().contains(requestedUserId))
                .map(exam -> new ExamSummary(exam.id(), exam.title()))
                .toList();
    }

    public ChangeResult moveWrongAnswers(
            Identity identity,
            String requestedUserId,
            String requestId,
            List<String> questionIds,
            WrongAnswerState target
    ) {
        verifyOwner(identity, requestedUserId);
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId is required");
        }
        if (!processedRequests.add(requestId)) {
            return new ChangeResult(false, 0);
        }

        List<WrongAnswer> answers = wrongAnswers.findAll(requestedUserId, questionIds);
        if (answers.size() != Set.copyOf(questionIds).size()) {
            processedRequests.remove(requestId);
            throw new IllegalArgumentException("unknown question included");
        }
        if (answers.stream().anyMatch(answer -> !answer.canMoveTo(target))) {
            processedRequests.remove(requestId);
            throw new IllegalStateException("invalid state transition");
        }

        answers.forEach(answer -> answer.moveTo(target));
        wrongAnswers.saveAll(answers);
        return new ChangeResult(true, answers.size());
    }

    private static void verifyOwner(Identity identity, String requestedUserId) {
        if (identity == null || !identity.userId().equals(requestedUserId)) {
            throw new SecurityException("token owner mismatch");
        }
    }

    public record Identity(String userId) {}
    public record ExamSummary(String id, String title) {}
    public record Exam(String id, String title, Set<String> allowedUserIds) {}
    public record ChangeResult(boolean changed, int count) {}

    public enum WrongAnswerState { ACTIVE, BOOKMARKED, TRASHED }

    public static final class WrongAnswer {
        private WrongAnswerState state;

        public WrongAnswer(WrongAnswerState state) {
            this.state = Objects.requireNonNull(state);
        }

        public boolean canMoveTo(WrongAnswerState target) {
            if (state == target) return false;
            return state != WrongAnswerState.TRASHED || target == WrongAnswerState.ACTIVE;
        }

        public void moveTo(WrongAnswerState target) {
            if (!canMoveTo(target)) throw new IllegalStateException("invalid state transition");
            state = target;
        }

        public WrongAnswerState state() { return state; }
    }

    public interface ExamCatalog {
        List<Exam> findOpenExams(String courseCode);
    }

    public interface WrongAnswerRepository {
        List<WrongAnswer> findAll(String userId, List<String> questionIds);
        void saveAll(List<WrongAnswer> answers);
    }
}

