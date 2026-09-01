package com.portfolio.exam.review;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ReviewPlanService {
    private final ReviewPlanRepository repository;

    public ReviewPlanService(ReviewPlanRepository repository) {
        this.repository = repository;
    }

    public ReviewPlan replaceConfiguration(
            String planId,
            List<ReviewPlan.FieldRequirement> fields,
            List<String> questionIds
    ) {
        repository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("review plan does not exist"));

        validateFields(fields);
        validateQuestionIds(questionIds);

        ReviewPlan replaced = new ReviewPlan(planId, fields, questionIds);
        repository.save(replaced);
        return replaced;
    }

    private void validateFields(List<ReviewPlan.FieldRequirement> fields) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("at least one field is required");
        }
        Set<String> fieldIds = new HashSet<>();
        for (ReviewPlan.FieldRequirement field : fields) {
            if (field.fieldId() == null || field.fieldId().isBlank()) {
                throw new IllegalArgumentException("fieldId is required");
            }
            if (field.requiredQuestionCount() < 0) {
                throw new IllegalArgumentException("question count cannot be negative");
            }
            if (!fieldIds.add(field.fieldId())) {
                throw new IllegalArgumentException("duplicated fieldId: " + field.fieldId());
            }
        }
    }

    private void validateQuestionIds(List<String> questionIds) {
        if (questionIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new IllegalArgumentException("questionId is required");
        }
        if (new HashSet<>(questionIds).size() != questionIds.size()) {
            throw new IllegalArgumentException("questionIds must be unique");
        }
    }
}

