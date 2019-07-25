package com.example.demo.dao;


public class ValidityRange {
    private long begin = Long.MAX_VALUE;
    private long end = Long.MIN_VALUE;

    boolean checkValid(long value) {
        return value > begin && value < end;
    }

    public void updateRange(long toEpochSecond) {
        if (toEpochSecond < this.end) {
            throw new IllegalStateException();
        }
        this.end = toEpochSecond;
        this.begin = end - 60;
    }
}
