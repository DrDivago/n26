package com.example.demo.cache;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.springframework.stereotype.Component;
import java.util.function.Predicate;

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

    @Override
    public long getTimestamp(Transaction transaction) {
        return readAction(transaction, (t, index) -> statisticsBuckets[index].getTimestamp());
    }

    @Override
    public void update(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].update(transaction));
    }

    @Override
    public void invalidate(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].invalidate());
    }

    @Override
    public void add(Transaction transaction) {
        writeAction(transaction, (t, index)-> statisticsBuckets[index].add(transaction));
    }

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

    public Statistics mapReduce(Predicate<Long> predicate) {
        Statistics totalStatistics = Statistics.Builder.newInstance().build();
        lock.readLock().lock();
        try {
            for (int i = 0; i < SIZE; i++) {
                Statistics newStatistic = statisticsBuckets[i].get();
                Long timestamp = statisticsBuckets[i].getTimestamp();
                if (predicate.test(timestamp) && newStatistic.getCount() > 0) {
                    reduce(totalStatistics, newStatistic);
                }
            }
            return totalStatistics;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void reduce(Statistics totalStatistics,Statistics newStatistic) {
        totalStatistics.merge(newStatistic);
    }
}
