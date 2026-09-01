package com.portfolio.exam.result;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class ResultStatusService {
    private final Clock clock;

    public ResultStatusService(Clock clock) {
        this.clock = clock;
    }

    public ChangeResult finalizeAbsence(List<ExamineeResult> results, List<String> examineeIds) {
        return change(results, examineeIds, true);
    }

    public ChangeResult cancelAbsenceFinalization(
            List<ExamineeResult> results,
            List<String> examineeIds
    ) {
        return change(results, examineeIds, false);
    }

    private ChangeResult change(
            List<ExamineeResult> results,
            List<String> examineeIds,
            boolean finalized
    ) {
        if (examineeIds.isEmpty()) {
            throw new IllegalArgumentException("at least one examinee is required");
        }
        var requested = SetSupport.unique(examineeIds);
        List<ExamineeResult> changed = new ArrayList<>();
        for (ExamineeResult result : results) {
            if (!requested.contains(result.examineeId())) {
                changed.add(result);
                continue;
            }
            if (result.attendance() != ExamineeResult.Attendance.ABSENT) {
                throw new IllegalStateException("only absent examinees can be finalized");
            }
            if (result.finalized() == finalized) {
                throw new IllegalStateException("result already has requested state");
            }
            changed.add(new ExamineeResult(result.examineeId(), result.attendance(), finalized));
        }
        if (changed.stream().filter(result -> requested.contains(result.examineeId())).count()
                != requested.size()) {
            throw new IllegalArgumentException("unknown examineeId");
        }
        return new ChangeResult(changed, new History(examineeIds, finalized, clock.instant()));
    }

    public record History(List<String> examineeIds, boolean finalized, Instant changedAt) {
        public History {
            examineeIds = List.copyOf(examineeIds);
        }
    }

    public record ChangeResult(List<ExamineeResult> results, History history) {
        public ChangeResult {
            results = List.copyOf(results);
        }
    }

    private static final class SetSupport {
        private static java.util.Set<String> unique(List<String> values) {
            var result = new java.util.HashSet<>(values);
            if (result.size() != values.size()) {
                throw new IllegalArgumentException("examineeIds must be unique");
            }
            return result;
        }
    }
}

