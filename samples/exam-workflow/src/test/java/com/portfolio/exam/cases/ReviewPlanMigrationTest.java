package com.portfolio.exam.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReviewPlanMigrationTest {
  static class Memory implements ReviewPlanMigration.Repository {
    ReviewPlanMigration.Composition saved;

    public boolean alreadyLinked(String t) {
      return saved != null;
    }

    public ReviewPlanMigration.Composition load(String s) {
      return new ReviewPlanMigration.Composition(
          List.of("field"), List.of("choice"), List.of("q1"));
    }

    public void linkAndStore(String t, String s, ReviewPlanMigration.Composition c) {
      saved = c;
    }
  }

  @Test
  void migratesEveryCollectionAndRejectsSecondLink() {
    var repository = new Memory();
    var service = new ReviewPlanMigration(repository);
    service.migrate("review", "authoring");
    assertEquals(List.of("field"), repository.saved.fields());
    assertEquals(List.of("choice"), repository.saved.types());
    assertEquals(List.of("q1"), repository.saved.questions());
    assertThrows(IllegalStateException.class, () -> service.migrate("review", "other"));
  }

  @Test
  void sourceFailureDoesNotAttemptSave() {
    var repository =
        new Memory() {
          public ReviewPlanMigration.Composition load(String s) {
            throw new IllegalStateException("missing");
          }
        };
    assertThrows(
        IllegalStateException.class, () -> new ReviewPlanMigration(repository).migrate("r", "s"));
    assertNull(repository.saved);
  }
}
