package com.portfolio.exam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ExamSessionTest {
    private static final Instant NOW = Instant.parse("2026-01-15T01:00:00Z");
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void acceptsAnswersOnlyWhileExamIsRunning() {
        ExamSession session = session();
        AnswerSubmission answer = answer("submission-1", "question-1", "A");

        assertThrows(IllegalStateException.class, () -> session.submit(answer));

        session.start();
        assertEquals(ExamSession.SubmissionResult.SAVED, session.submit(answer));
        assertEquals("A", session.answers().get("question-1").answer());

        session.finish();
        assertThrows(IllegalStateException.class,
                () -> session.submit(answer("submission-2", "question-2", "B")));
    }

    @Test
    void ignoresARepeatedSubmissionId() {
        ExamSession session = session();
        session.start();

        AnswerSubmission first = answer("same-request", "question-1", "A");
        AnswerSubmission repeated = answer("same-request", "question-1", "B");

        assertEquals(ExamSession.SubmissionResult.SAVED, session.submit(first));
        assertEquals(ExamSession.SubmissionResult.DUPLICATE, session.submit(repeated));
        assertEquals("A", session.answers().get("question-1").answer());
    }

    @Test
    void updatesAnAnswerWithANewSubmissionId() {
        ExamSession session = session();
        session.start();

        session.submit(answer("submission-1", "question-1", "A"));
        session.submit(answer("submission-2", "question-1", "C"));

        assertEquals("C", session.answers().get("question-1").answer());
        assertEquals(1, session.answers().size());
    }

    private ExamSession session() {
        return new ExamSession(NOW.minusSeconds(60), NOW.plusSeconds(3600), clock);
    }

    private AnswerSubmission answer(String submissionId, String questionId, String value) {
        return new AnswerSubmission(submissionId, questionId, value, NOW);
    }
}
