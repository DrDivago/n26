package com.n26.cache;

import com.n26.model.Statistics;
import com.n26.model.Transaction;

import java.time.LocalDateTime;

class StatisticsBucket {

    private Statistics statistics;
    private LocalDateTime timestamp;

    StatisticsBucket() {
        invalidate();
    }

    void invalidate() {
        statistics = Statistics.Builder.newInstance().build();
        timestamp = null;
    }

    void update(Transaction transaction) {
        statistics.update(transaction.getAmount());
    }

    LocalDateTime getTimestamp() {
        return timestamp;
    }

    void add(Transaction transaction) {
        update(transaction);
        timestamp = transaction.getTimestamp();
    }

    public Statistics get() {
        return statistics;
    }
}
