package com.portfolio.exam.readiness;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public final class ExamReadinessService {
    private final Clock clock;

    public ExamReadinessService(Clock clock) {
        this.clock = clock;
    }

    public AssignmentResult assign(
            Candidate candidate,
            Room room,
            String seatNumber,
            String candidateNumber,
            List<SeatAssignment> current
    ) {
        validateCandidate(candidate);
        if (room.capacity() < 1) throw new IllegalArgumentException("room capacity must be positive");
        if (seatNumber == null || seatNumber.isBlank()) throw new IllegalArgumentException("seat is required");
        if (candidateNumber == null || candidateNumber.isBlank()) throw new IllegalArgumentException("candidate number is required");

        var candidates = new HashSet<String>();
        var seats = new HashSet<String>();
        var numbers = new HashSet<String>();
        for (SeatAssignment value : current) {
            if (!candidates.add(value.candidateId())) throw new IllegalArgumentException("candidate is assigned more than once");
            if (!numbers.add(value.candidateNumber())) throw new IllegalArgumentException("candidate number is duplicated");
            if (value.roomId().equals(room.roomId()) && !seats.add(value.seatNumber())) {
                throw new IllegalArgumentException("seat is duplicated");
            }
        }

        SeatAssignment previous = current.stream()
                .filter(value -> value.candidateId().equals(candidate.candidateId()))
                .findFirst().orElse(null);
        if (previous != null && previous.roomId().equals(room.roomId())
                && previous.seatNumber().equals(seatNumber)
                && previous.candidateNumber().equals(candidateNumber)) {
            return new AssignmentResult(List.copyOf(current), false, null);
        }
        if (current.stream().anyMatch(value -> !value.candidateId().equals(candidate.candidateId())
                && value.candidateNumber().equals(candidateNumber))) {
            throw new IllegalStateException("candidate number is already used");
        }
        if (current.stream().anyMatch(value -> !value.candidateId().equals(candidate.candidateId())
                && value.roomId().equals(room.roomId()) && value.seatNumber().equals(seatNumber))) {
            throw new IllegalStateException("seat is already occupied");
        }
        long assignedToRoom = current.stream()
                .filter(value -> value.roomId().equals(room.roomId()))
                .filter(value -> !value.candidateId().equals(candidate.candidateId()))
                .count();
        if (assignedToRoom >= room.capacity()) throw new IllegalStateException("room capacity exceeded");

        var changed = new ArrayList<>(current);
        changed.removeIf(value -> value.candidateId().equals(candidate.candidateId()));
        var next = new SeatAssignment(candidate.candidateId(), room.roomId(), seatNumber, candidateNumber);
        changed.add(next);
        return new AssignmentResult(List.copyOf(changed), true,
                new AssignmentHistory(candidate.candidateId(), previous, next, clock.instant()));
    }

    private void validateCandidate(Candidate candidate) {
        Objects.requireNonNull(candidate, "candidate is required");
        if (candidate.candidateId() == null || candidate.candidateId().isBlank()
                || candidate.examId() == null || candidate.examId().isBlank()
                || candidate.scheduleId() == null || candidate.scheduleId().isBlank()) {
            throw new IllegalArgumentException("candidate exam data is incomplete");
        }
    }

    public record Candidate(String candidateId, String examId, String scheduleId) {}
    public record Room(String roomId, int capacity) {}
    public record SeatAssignment(String candidateId, String roomId, String seatNumber, String candidateNumber) {}
    public record AssignmentHistory(String candidateId, SeatAssignment before, SeatAssignment after, Instant changedAt) {}
    public record AssignmentResult(List<SeatAssignment> assignments, boolean changed, AssignmentHistory history) {}
}
