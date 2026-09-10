package com.portfolio.exam.cases;

import java.util.List;

/** Combines exam metadata, a filtered total and only the requested result page. */
public final class ExamStatisticsQuery {
  public record Filter(String exam, String status, int page, int size) {
    public Filter {
      if (exam == null || exam.isBlank() || page < 1 || size < 1 || size > 200)
        throw new IllegalArgumentException("invalid query");
    }
  }

  public record Result(String examTitle, long total, List<String> rows) {}

  public interface Repository {
    long count(Filter filter);

    String title(String exam);

    List<String> rows(Filter filter, long offset);
  }

  public Result search(Repository repository, Filter filter) {
    long total = repository.count(filter);
    String title = repository.title(filter.exam());
    long offset = (long) (filter.page() - 1) * filter.size();
    return new Result(
        title, total, offset >= total ? List.of() : List.copyOf(repository.rows(filter, offset)));
  }
}
