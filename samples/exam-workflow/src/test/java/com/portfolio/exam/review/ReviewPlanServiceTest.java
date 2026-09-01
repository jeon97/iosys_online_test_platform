package com.portfolio.exam.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ReviewPlanServiceTest {
    @Test
    void replacesFieldsAndQuestionsAfterValidation() {
        MemoryRepository repository = new MemoryRepository();
        repository.save(new ReviewPlan("plan-1", List.of(), List.of()));
        ReviewPlanService service = new ReviewPlanService(repository);

        ReviewPlan result = service.replaceConfiguration(
                "plan-1",
                List.of(new ReviewPlan.FieldRequirement("network", 10)),
                List.of("question-1", "question-2")
        );

        assertEquals(10, result.fields().get(0).requiredQuestionCount());
        assertEquals(2, result.questionIds().size());
    }

    @Test
    void rejectsDuplicatedQuestionsBeforeSaving() {
        MemoryRepository repository = new MemoryRepository();
        repository.save(new ReviewPlan("plan-1", List.of(), List.of()));
        ReviewPlanService service = new ReviewPlanService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.replaceConfiguration(
                "plan-1",
                List.of(new ReviewPlan.FieldRequirement("network", 10)),
                List.of("question-1", "question-1")
        ));
    }

    private static final class MemoryRepository implements ReviewPlanRepository {
        private final Map<String, ReviewPlan> plans = new HashMap<>();

        @Override
        public Optional<ReviewPlan> findById(String planId) {
            return Optional.ofNullable(plans.get(planId));
        }

        @Override
        public void save(ReviewPlan plan) {
            plans.put(plan.planId(), plan);
        }
    }
}

