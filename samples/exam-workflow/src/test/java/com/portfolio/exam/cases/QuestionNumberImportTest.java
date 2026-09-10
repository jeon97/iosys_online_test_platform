package com.portfolio.exam.cases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class QuestionNumberImportTest {
  static class Memory implements QuestionNumberImport.Repository {
    List<QuestionNumberImport.Question> saved =
        List.of(new QuestionNumberImport.Question("old", "f0"));

    public Optional<QuestionNumberImport.Question> resolve(String n) {
      return n.equals("missing")
          ? Optional.empty()
          : Optional.of(new QuestionNumberImport.Question(n, "f1"));
    }

    public void replaceAll(String p, List<QuestionNumberImport.Question> q) {
      saved = q;
    }
  }

  @Test
  void resolvesFieldMetadataAndPreservesInputOrder() {
    var memory = new Memory();
    new QuestionNumberImport(memory).replace("p", List.of("2", "1"));
    assertEquals(
        List.of(
            new QuestionNumberImport.Question("2", "f1"),
            new QuestionNumberImport.Question("1", "f1")),
        memory.saved);
  }

  @Test
  void lateInvalidRowDoesNotRemovePreviousPlan() {
    var memory = new Memory();
    assertThrows(
        IllegalArgumentException.class,
        () -> new QuestionNumberImport(memory).replace("p", List.of("1", "missing")));
    assertEquals("old", memory.saved.get(0).number());
  }
}
