package com.portfolio.exam.result;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResultStatusServiceTest {
    private final ResultStatusService service = new ResultStatusService(
            Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC)
    );

    @Test
    void finalizesAndCancelsAbsentResult() {
        List<ExamineeResult> initial = List.of(
                new ExamineeResult("examinee-1", ExamineeResult.Attendance.ABSENT, false)
        );

        var finalized = service.finalizeAbsence(initial, List.of("examinee-1"));
        assertTrue(finalized.results().get(0).finalized());

        var canceled = service.cancelAbsenceFinalization(
                finalized.results(), List.of("examinee-1")
        );
        assertFalse(canceled.results().get(0).finalized());
    }

    @Test
    void rejectsPresentExamineeAndRepeatedChange() {
        List<ExamineeResult> present = List.of(
                new ExamineeResult("examinee-1", ExamineeResult.Attendance.PRESENT, false)
        );
        assertThrows(IllegalStateException.class,
                () -> service.finalizeAbsence(present, List.of("examinee-1")));

        List<ExamineeResult> alreadyFinalized = List.of(
                new ExamineeResult("examinee-2", ExamineeResult.Attendance.ABSENT, true)
        );
        assertThrows(IllegalStateException.class,
                () -> service.finalizeAbsence(alreadyFinalized, List.of("examinee-2")));
    }
}
