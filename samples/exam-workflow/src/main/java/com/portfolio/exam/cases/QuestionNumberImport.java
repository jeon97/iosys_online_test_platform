package com.portfolio.exam.cases;

import java.util.*;

/** Resolves spreadsheet question numbers to metadata before replacing review targets. */
public final class QuestionNumberImport {
  public record Question(String number, String field) {}

  public interface Repository {
    Optional<Question> resolve(String number);

    /** Production adapter must replace the target collection in one transaction. */
    void replaceAll(String plan, List<Question> questions);
  }

  private final Repository repository;

  public QuestionNumberImport(Repository repository) {
    this.repository = repository;
  }

  public void replace(String plan, List<String> numbers) {
    if (numbers.isEmpty() || new HashSet<>(numbers).size() != numbers.size())
      throw new IllegalArgumentException("nonempty unique numbers required");
    var questions = new ArrayList<Question>();
    for (String number : numbers) {
      if (number == null || number.isBlank()) throw new IllegalArgumentException("number required");
      questions.add(
          repository
              .resolve(number)
              .orElseThrow(() -> new IllegalArgumentException("unknown number")));
    }
    // Unlike the original delete-then-insert transaction, validation precedes replacement here.
    repository.replaceAll(plan, List.copyOf(questions));
  }
}
