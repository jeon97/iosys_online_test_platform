package com.portfolio.exam.cases;

import java.util.*;

/**
 * Quotient/remainder allocation preserves question order and gives earlier reviewers the remainder.
 */
public final class ContiguousReviewerAssignment {
  public record Allocation(String reviewer, List<String> questions) {}

  public List<Allocation> assign(List<String> reviewers, List<String> questions) {
    validate(reviewers);
    validate(questions);
    if (reviewers.isEmpty()) {
      if (!questions.isEmpty()) throw new IllegalArgumentException("reviewers required");
      return List.of();
    }
    int base = questions.size() / reviewers.size();
    int remainder = questions.size() % reviewers.size();
    int cursor = 0;
    var result = new ArrayList<Allocation>();
    for (int index = 0; index < reviewers.size(); index++) {
      int end = cursor + base + (index < remainder ? 1 : 0);
      result.add(new Allocation(reviewers.get(index), List.copyOf(questions.subList(cursor, end))));
      cursor = end;
    }
    return List.copyOf(result);
  }

  private void validate(List<String> values) {
    if (values.stream().anyMatch(v -> v == null || v.isBlank())
        || new HashSet<>(values).size() != values.size())
      throw new IllegalArgumentException("unique nonblank identifiers required");
  }
}
