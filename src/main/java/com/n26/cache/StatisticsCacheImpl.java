package com.n26.cache;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Predicate;

/**
 * Cache implemented with a array of SIZE bucket
 * when a new Transaction is added an index is calculated and the transaction is added on the bucket
 * add, update, invalidate, deleteAll acquire a write lock
 * getTimestamp and mapReduce get a readlock
 */
@Component
public class StatisticsCacheImpl extends BaseStatisticsCache implements StatisticsCache<Transaction, Statistics> {

    private final StatisticsBucket[] statisticsBuckets;
    private static final int SIZE = 60;


    public StatisticsCacheImpl() {
        statisticsBuckets = new StatisticsBucket[SIZE];
        for (int i = 0; i < SIZE; i++) {
            statisticsBuckets[i] = new StatisticsBucket();
        }
    }

    /**
     * Calculate bucket, acquire read lock, access the bucket and get the timestamp
     * @param transaction used to calculate the index
     * @return timestamp for the bucket
     */
    @Override
    public LocalDateTime getTimestamp(Transaction transaction) {
        return readAction(transaction, (t, index) -> statisticsBuckets[index].getTimestamp());
    }

    /**
     * Update statistics of the transaction in bucket calculated from the timestamp of the transaction
     * @param transaction Transaction used for update the state of cache
     */
    @Override
    public void update(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].update(transaction));
    }

    /**
     * Reset the statistics of the transaction in bucket calculated from the timestamp of the transaction
     * @param transaction Transaction used for update the state of cache
     */
    @Override
    public void invalidate(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].invalidate());
    }

    /**
     * Add the timestamp on the bucket
     * Update the transaction
     * @param transaction Transaction used for update the state of cache
     */
    @Override
    public void add(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].add(transaction));
    }

    /**
     * Invalidate oll the buckets statistics
     */
    @Override
    public void deleteAll() {
        lock.writeLock().lock();
        try {
            for (int i = 0; i < SIZE; i++) {
                statisticsBuckets[i].invalidate();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Filter the statistics with predicate and reduce all the statistics in bucket in one statistics
     * @param predicate used to filter the array
     * @return reduced statistics
     */
    public Statistics mapReduce(Predicate<LocalDateTime> predicate) {
        Statistics totalStatistics = Statistics.Builder.newInstance().build();
        lock.readLock().lock();
        try {
            for (int i = 0; i < SIZE; i++) {
                Statistics newStatistic = statisticsBuckets[i].get();
                LocalDateTime timestamp = statisticsBuckets[i].getTimestamp();
                if (timestamp != null && newStatistic.getCount() > 0 && predicate.test(timestamp) ) {
                    reduce(totalStatistics, newStatistic);
                }
            }
            return totalStatistics;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void reduce(Statistics totalStatistics, Statistics newStatistic) {
        totalStatistics.merge(newStatistic);
    }
}
