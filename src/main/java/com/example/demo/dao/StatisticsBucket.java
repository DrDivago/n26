package com.example.demo.dao;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StatisticsBucket {

    private BigDecimal sum;
    private BigDecimal avg;
    private long count;
    private BigDecimal min;
    private BigDecimal max;
    private long timestamp;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public StatisticsBucket() {
        invalidate();
    }

    public void invalidate() {
        lock.writeLock().lock();
        this.sum = BigDecimal.ZERO;
        this.avg = BigDecimal.ZERO;
        this.min = BigDecimal.valueOf(Double.MAX_VALUE);
        this.max = BigDecimal.valueOf(Double.MIN_VALUE);
        timestamp = 0;
        lock.writeLock().unlock();
    }

    public void update(Transaction transaction) {
        lock.writeLock().lock();
        count += 1;
        sum = sum.add(transaction.getAmount());
        avg = sum.divide(new BigDecimal(count), BigDecimal.ROUND_UP);
        if (transaction.getAmount().compareTo(min) < 0) {
            min = transaction.getAmount();
        }

        if (transaction.getAmount().compareTo(max) > 0) {
            max = transaction.getAmount();
        }
        lock.writeLock().unlock();
    }

    public long getTimestamp() {
        lock.readLock().lock();
        try {
            return timestamp;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void add(Transaction transaction) {
        lock.writeLock().lock();
        count += 1;
        sum = transaction.getAmount();
        avg = transaction.getAmount();
        if (transaction.getAmount().compareTo(min) < 0) {
            min = transaction.getAmount();
        }

        if (transaction.getAmount().compareTo(max) > 0) {
            max = transaction.getAmount();
        }
        lock.writeLock().unlock();

    }

    public Statistics get() {
        lock.readLock().lock();
        try {
            Statistics statistics = Statistics.Builder.newInstance()
                    .withSum(sum)
                    .withAvg(avg)
                    .withCount(count)
                    .withMax(max)
                    .withMin(min)
                    .build();
            return statistics;
        } finally {
            lock.readLock().unlock();
        }
    }
}
