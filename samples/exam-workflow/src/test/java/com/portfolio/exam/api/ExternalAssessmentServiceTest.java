package com.portfolio.exam.api;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExternalAssessmentServiceTest {
    @Test
    void returnsOnlyExamsOwnedByTokenUser() {
        var catalog = (ExternalAssessmentService.ExamCatalog) course -> List.of(
                new ExternalAssessmentService.Exam("exam-1", "모의평가", Set.of("user-1")),
                new ExternalAssessmentService.Exam("exam-2", "다른 사용자 평가", Set.of("user-2"))
        );
        var service = new ExternalAssessmentService(catalog, new MemoryWrongAnswers());

        var result = service.availableExams(
                new ExternalAssessmentService.Identity("user-1"), "user-1", "course-1");

        assertEquals(List.of("exam-1"), result.stream().map(ExternalAssessmentService.ExamSummary::id).toList());
    }

    @Test
    void rejectsTokenOwnerMismatch() {
        var service = new ExternalAssessmentService(course -> List.of(), new MemoryWrongAnswers());

        assertThrows(SecurityException.class, () -> service.availableExams(
                new ExternalAssessmentService.Identity("user-1"), "user-2", "course-1"));
    }

    @Test
    void duplicateRequestDoesNotApplyChangeTwice() {
        var repository = new MemoryWrongAnswers();
        repository.answers.add(new ExternalAssessmentService.WrongAnswer(
                ExternalAssessmentService.WrongAnswerState.ACTIVE));
        var service = new ExternalAssessmentService(course -> List.of(), repository);
        var identity = new ExternalAssessmentService.Identity("user-1");

        var first = service.moveWrongAnswers(identity, "user-1", "request-1", List.of("q-1"),
                ExternalAssessmentService.WrongAnswerState.BOOKMARKED);
        var second = service.moveWrongAnswers(identity, "user-1", "request-1", List.of("q-1"),
                ExternalAssessmentService.WrongAnswerState.BOOKMARKED);

        assertTrue(first.changed());
        assertFalse(second.changed());
        assertEquals(1, repository.saveCount);
    }

    private static final class MemoryWrongAnswers implements ExternalAssessmentService.WrongAnswerRepository {
        private final List<ExternalAssessmentService.WrongAnswer> answers = new ArrayList<>();
        private int saveCount;

        @Override
        public List<ExternalAssessmentService.WrongAnswer> findAll(String userId, List<String> questionIds) {
            return answers.subList(0, Math.min(answers.size(), questionIds.size()));
        }

        @Override
        public void saveAll(List<ExternalAssessmentService.WrongAnswer> answers) {
            saveCount++;
        }
    }
}

