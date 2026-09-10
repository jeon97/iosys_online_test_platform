package com.portfolio.exam.cases;

import java.util.List;

/** Loads the fields, types and questions of a source plan and links them to a review plan. */
public final class ReviewPlanMigration {
  public record Composition(List<String> fields, List<String> types, List<String> questions) {
    public Composition {
      fields = List.copyOf(fields);
      types = List.copyOf(types);
      questions = List.copyOf(questions);
    }
  }

  public interface Repository {
    boolean alreadyLinked(String target);

    Composition load(String source);

    /** Must atomically store the source link and all three collections. */
    void linkAndStore(String target, String source, Composition composition);
  }

  private final Repository repository;

  public ReviewPlanMigration(Repository repository) {
    this.repository = repository;
  }

  public void migrate(String target, String source) {
    if (target.isBlank() || source.isBlank()) throw new IllegalArgumentException("plan required");
    if (repository.alreadyLinked(target)) throw new IllegalStateException("plan already linked");
    repository.linkAndStore(target, source, repository.load(source));
  }
}
