package com.n26.util;

import java.time.LocalDateTime;

public class ValidityRange {
    private LocalDateTime begin;
    private LocalDateTime end;

    public boolean isValid(LocalDateTime value) {
        if (value == null)
            return false;
        return value.isAfter(begin) && value.isBefore(end);
    }

    public void updateRange(LocalDateTime localDateTime) {
        this.end = localDateTime;
        this.begin = end.minusMinutes(1);
    }
}
