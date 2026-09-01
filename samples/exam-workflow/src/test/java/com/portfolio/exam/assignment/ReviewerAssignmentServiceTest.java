package com.portfolio.exam.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ReviewerAssignmentServiceTest {
    private final ReviewerAssignmentService service = new ReviewerAssignmentService();

    @Test
    void distributesQuestionsEvenly() {
        List<ReviewerAssignment> assignments = service.assignEvenly(
                List.of("q1", "q2", "q3", "q4", "q5"),
                List.of("reviewer-a", "reviewer-b"),
                List.of()
        );

        Map<String, Long> countByReviewer = assignments.stream()
                .collect(Collectors.groupingBy(ReviewerAssignment::reviewerId, Collectors.counting()));

        assertEquals(3, countByReviewer.get("reviewer-a"));
        assertEquals(2, countByReviewer.get("reviewer-b"));
    }

    @Test
    void rejectsDuplicatedExistingAssignments() {
        List<ReviewerAssignment> duplicated = List.of(
                new ReviewerAssignment("q1", "reviewer-a"),
                new ReviewerAssignment("q1", "reviewer-b")
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.assignEvenly(List.of(), List.of("reviewer-a"), duplicated));
    }
}
