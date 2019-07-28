package com.example.demo.cache;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;

import java.time.ZoneOffset;

class StatisticsBucket {

    private Statistics statistics;
    private long timestamp;

    public StatisticsBucket() {
        invalidate();
    }

    public void invalidate() {
        statistics = Statistics.Builder.newInstance().build();
    }

    public void update(Transaction transaction) {
        statistics.update(transaction.getAmount());
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void add(Transaction transaction) {
        update(transaction);
        timestamp = transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC);
    }

    public Statistics get() {
        return statistics;
    }
}
