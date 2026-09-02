package com.portfolio.exam.readiness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExamReadinessServiceTest {
    private final ExamReadinessService service = new ExamReadinessService(
            Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void assignsAndRecordsASeatChange() {
        var result = service.assign(candidate(), new ExamReadinessService.Room("room-1", 2),
                "A-01", "2026-0001", List.of());
        assertEquals(1, result.assignments().size());
        assertNotNull(result.history());
    }

    @Test
    void treatsTheSameAssignmentAsIdempotent() {
        var value = new ExamReadinessService.SeatAssignment("user-1", "room-1", "A-01", "2026-0001");
        var result = service.assign(candidate(), new ExamReadinessService.Room("room-1", 2),
                "A-01", "2026-0001", List.of(value));
        assertFalse(result.changed());
    }

    @Test
    void rejectsOccupiedSeatAndIncompleteCandidate() {
        var occupied = new ExamReadinessService.SeatAssignment("user-2", "room-1", "A-01", "2026-0002");
        assertThrows(IllegalStateException.class, () -> service.assign(candidate(),
                new ExamReadinessService.Room("room-1", 2), "A-01", "2026-0001", List.of(occupied)));
        assertThrows(IllegalArgumentException.class, () -> service.assign(
                new ExamReadinessService.Candidate("user-1", "", "schedule-1"),
                new ExamReadinessService.Room("room-1", 2), "A-02", "2026-0001", List.of()));
    }

    private ExamReadinessService.Candidate candidate() {
        return new ExamReadinessService.Candidate("user-1", "exam-1", "schedule-1");
    }
}
