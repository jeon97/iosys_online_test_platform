package com.portfolio.exam.assignment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReviewerAssignmentService {
    public List<ReviewerAssignment> assignEvenly(
            List<String> questionIds,
            List<String> reviewerIds,
            List<ReviewerAssignment> existingAssignments
    ) {
        requireUniqueValues(questionIds, "questionIds");
        requireUniqueValues(reviewerIds, "reviewerIds");
        if (reviewerIds.isEmpty()) {
            throw new IllegalArgumentException("at least one reviewer is required");
        }

        Map<String, Integer> assignedCount = new HashMap<>();
        reviewerIds.forEach(id -> assignedCount.put(id, 0));

        Set<String> assignedQuestions = new HashSet<>();
        for (ReviewerAssignment assignment : existingAssignments) {
            if (!assignedQuestions.add(assignment.questionId())) {
                throw new IllegalArgumentException("question is already assigned");
            }
            assignedCount.computeIfPresent(assignment.reviewerId(), (id, count) -> count + 1);
        }

        List<ReviewerAssignment> result = new ArrayList<>(existingAssignments);
        for (String questionId : questionIds) {
            if (assignedQuestions.contains(questionId)) {
                continue;
            }
            String reviewerId = reviewerIds.stream()
                    .min(Comparator.comparingInt(assignedCount::get))
                    .orElseThrow();
            result.add(new ReviewerAssignment(questionId, reviewerId));
            assignedQuestions.add(questionId);
            assignedCount.compute(reviewerId, (id, count) -> count + 1);
        }
        return List.copyOf(result);
    }

    private void requireUniqueValues(List<String> values, String fieldName) {
        if (values.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException(fieldName + " cannot contain blank values");
        }
        if (new HashSet<>(values).size() != values.size()) {
            throw new IllegalArgumentException(fieldName + " must be unique");
        }
    }
}

