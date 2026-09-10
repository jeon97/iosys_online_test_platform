package com.portfolio.exam.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ContiguousReviewerAssignmentTest {
  @Test
  void sevenQuestionsBecomeContiguousThreeTwoTwoGroups() {
    var result =
        new ContiguousReviewerAssignment()
            .assign(List.of("a", "b", "c"), List.of("1", "2", "3", "4", "5", "6", "7"));
    assertEquals(List.of("1", "2", "3"), result.get(0).questions());
    assertEquals(List.of("4", "5"), result.get(1).questions());
    assertEquals(List.of("6", "7"), result.get(2).questions());
  }

  @Test
  void moreReviewersThanQuestionsLeavesEmptyGroupsAndDuplicateInputIsRejected() {
    var service = new ContiguousReviewerAssignment();
    assertEquals(List.of(), service.assign(List.of("a", "b"), List.of("1")).get(1).questions());
    assertThrows(IllegalArgumentException.class, () -> service.assign(List.of(), List.of("1")));
    assertThrows(
        IllegalArgumentException.class, () -> service.assign(List.of("a"), List.of("1", "1")));
  }
}
