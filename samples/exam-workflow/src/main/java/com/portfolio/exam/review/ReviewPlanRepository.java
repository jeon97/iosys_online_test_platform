package com.portfolio.exam.review;

import java.util.Optional;

public interface ReviewPlanRepository {
    Optional<ReviewPlan> findById(String planId);

    void save(ReviewPlan plan);
}

