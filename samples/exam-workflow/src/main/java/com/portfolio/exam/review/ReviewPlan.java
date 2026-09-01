package com.portfolio.exam.review;

import java.util.List;

public record ReviewPlan(String planId, List<FieldRequirement> fields, List<String> questionIds) {
    public ReviewPlan {
        fields = List.copyOf(fields);
        questionIds = List.copyOf(questionIds);
    }

    public record FieldRequirement(String fieldId, int requiredQuestionCount) {
    }
}

