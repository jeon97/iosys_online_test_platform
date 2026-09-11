package com.portfolio.exam.cases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TicketPresentationTest {
  @Test
  void exemptionLabelIsAppliedOnlyToTheRelevantExamType() {
    var presenter = new TicketPresentation();
    assertEquals(
        "[ ] attend / [x] exempt",
        presenter
            .prepare(new TicketPresentation.Input("EXEMPT", false, null, null, null), id -> false)
            .stage());
    assertEquals(
        "EXEMPT",
        presenter
            .prepare(new TicketPresentation.Input("EXEMPT", true, null, null, null), id -> false)
            .stage());
  }

  @Test
  void missingPhotoIsOmittedAndExistingMapAssetTakesPriority() {
    var view =
        new TicketPresentation()
            .prepare(
                new TicketPresentation.Input("ATTEND", false, "photo-demo", "map-demo", "url-demo"),
                id -> id.equals("map-demo"));
    assertNull(view.photo());
    assertEquals("map-demo", view.map());
  }

  @Test
  void urlFallbackOccursOnlyWhenLocalMapWasNotSpecified() {
    var presenter = new TicketPresentation();
    assertNull(
        presenter
            .prepare(
                new TicketPresentation.Input("ATTEND", false, null, "missing-map", "url-demo"),
                id -> false)
            .map());
    assertEquals(
        "url-demo",
        presenter
            .prepare(
                new TicketPresentation.Input("ATTEND", false, null, null, "url-demo"), id -> false)
            .map());
  }
}
