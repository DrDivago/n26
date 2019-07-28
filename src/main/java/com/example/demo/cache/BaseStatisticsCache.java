package com.example.demo.cache;

import com.example.demo.model.Transaction;
import org.springframework.util.Assert;

import java.time.ZoneOffset;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BaseStatisticsCache {

    private static final int SIZE = 60;
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    void writeAction(Transaction transaction, BiConsumer<Transaction, Integer> consumer) {
        checkAssertion(transaction);
        int index = calculateBucket(transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC));
        lock.writeLock().lock();
        try {
            consumer.accept(transaction, index);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    Long readAction(Transaction transaction, BiFunction<Transaction, Integer, Long> consumer) {
        checkAssertion(transaction);
        int index = calculateBucket(transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC));
        lock.readLock().lock();
        try {
            return consumer.apply(transaction, index);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkAssertion(Transaction transaction) {
        Assert.notNull(transaction, "Transaction is null");
        Assert.notNull(transaction.getTimestamp(), "Timestamp is null");
    }

    private int calculateBucket(long timestamp) {
        return (int)(timestamp % SIZE);
    }
}
