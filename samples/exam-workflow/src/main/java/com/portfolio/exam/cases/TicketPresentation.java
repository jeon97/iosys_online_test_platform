package com.portfolio.exam.cases;

import java.util.function.Predicate;

/** Source-derived report preparation; assets are synthetic IDs, never filesystem paths. */
public final class TicketPresentation {
  public record Input(
      String stage, boolean privateExam, String photo, String localMap, String mapUrl) {}

  public record View(String stage, String photo, String map) {}

  public View prepare(Input input, Predicate<String> existingAsset) {
    String stage = input.stage();
    if (!input.privateExam()) {
      if ("ATTEND".equals(stage)) stage = "[x] attend / [ ] exempt";
      else if ("EXEMPT".equals(stage)) stage = "[ ] attend / [x] exempt";
    }
    String photo =
        input.photo() != null && existingAsset.test(input.photo()) ? input.photo() : null;
    String map =
        input.localMap() == null
            ? input.mapUrl()
            : existingAsset.test(input.localMap()) ? input.localMap() : null;
    return new View(stage, photo, map);
  }
}
