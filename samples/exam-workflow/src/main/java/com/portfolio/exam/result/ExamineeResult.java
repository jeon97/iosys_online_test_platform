package com.portfolio.exam.result;

public record ExamineeResult(String examineeId, Attendance attendance, boolean finalized) {
    public enum Attendance {
        PRESENT, ABSENT
    }
}

