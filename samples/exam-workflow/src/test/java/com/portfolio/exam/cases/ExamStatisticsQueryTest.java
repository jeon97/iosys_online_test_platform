package com.portfolio.exam.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExamStatisticsQueryTest {
  @Test
  void countsAndListsUseTheSameFilter() {
    var filter = new ExamStatisticsQuery.Filter("exam-a", "finished", 2, 10);
    var result =
        new ExamStatisticsQuery()
            .search(
                new ExamStatisticsQuery.Repository() {
                  public long count(ExamStatisticsQuery.Filter f) {
                    assertSame(filter, f);
                    return 12;
                  }

                  public String title(String e) {
                    assertEquals("exam-a", e);
                    return "Demo";
                  }

                  public List<String> rows(ExamStatisticsQuery.Filter f, long offset) {
                    assertSame(filter, f);
                    assertEquals(10, offset);
                    return List.of("r11", "r12");
                  }
                },
                filter);
    assertEquals(12, result.total());
    assertEquals(2, result.rows().size());
  }

  @Test
  void emptyResultsStillIncludeExamMetadataWithoutReadingRows() {
    var result =
        new ExamStatisticsQuery()
            .search(
                new ExamStatisticsQuery.Repository() {
                  public long count(ExamStatisticsQuery.Filter f) {
                    return 0;
                  }

                  public String title(String e) {
                    return "Demo";
                  }

                  public List<String> rows(ExamStatisticsQuery.Filter f, long o) {
                    fail("no row read");
                    return List.of();
                  }
                },
                new ExamStatisticsQuery.Filter("exam-a", "all", 1, 10));
    assertEquals("Demo", result.examTitle());
    assertTrue(result.rows().isEmpty());
  }
}
