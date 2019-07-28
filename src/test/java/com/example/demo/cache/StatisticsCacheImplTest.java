package com.example.demo.cache;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

public class StatisticsCacheImplTest {

    @Test(expected = IllegalArgumentException.class)
    public void getTimestamp_transaction_null() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        statisticsCache.getTimestamp(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTimestamp_null() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        Transaction t = new Transaction();
        statisticsCache.getTimestamp(t);
    }

    @Test
    public void getTimestamp_valid() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        Transaction t = new Transaction();
        t.setTimestamp(LocalDateTime.now());
        long timestamp = statisticsCache.getTimestamp(t);
        Assert.assertEquals(timestamp, 0);
    }

    @Test
    public void add_and_mapReduce() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        LocalDateTime now = LocalDateTime.now();

        Transaction t = new Transaction();
        t.setTimestamp(now);
        t.setAmount(BigDecimal.valueOf(10));

        statisticsCache.add(t);

        Statistics statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 1);
        Assert.assertEquals(statistics.getSum().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statisticsCache.getTimestamp(t), now.toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void invalidate() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        LocalDateTime now = LocalDateTime.now();

        Transaction t = new Transaction();
        t.setTimestamp(now);
        t.setAmount(BigDecimal.valueOf(10));

        statisticsCache.add(t);
        statisticsCache.invalidate(t);

        Statistics statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 0);
        Assert.assertEquals(statistics.getSum().doubleValue(), 0, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 0, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), Double.MAX_VALUE, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), Double.MIN_VALUE, 0.01);
    }

    @Test
    public void add_update_mapReduce() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        LocalDateTime now = LocalDateTime.now();

        Transaction t = new Transaction();
        t.setTimestamp(now);
        t.setAmount(BigDecimal.valueOf(10));

        statisticsCache.add(t);

        Statistics statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 1);
        Assert.assertEquals(statistics.getSum().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statisticsCache.getTimestamp(t), now.toEpochSecond(ZoneOffset.UTC));

        Transaction t2 = new Transaction();
        t2.setTimestamp(now);
        t2.setAmount(BigDecimal.valueOf(20));

        statisticsCache.update(t2);
        statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 2);
        Assert.assertEquals(statistics.getSum().doubleValue(), 30.0, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 15.0, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), 20.0, 0.01);
        Assert.assertEquals(statisticsCache.getTimestamp(t), now.toEpochSecond(ZoneOffset.UTC));
        Assert.assertEquals(statisticsCache.getTimestamp(t), statisticsCache.getTimestamp(t2));

    }

    @Test
    public void deleteAll() {
        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();
        LocalDateTime now = LocalDateTime.now();

        Transaction t1 = new Transaction();
        t1.setTimestamp(now);
        t1.setAmount(BigDecimal.valueOf(10));

        Transaction t2 = new Transaction();
        t2.setTimestamp(now.minusSeconds(30));
        t2.setAmount(BigDecimal.valueOf(20));

        statisticsCache.add(t1);
        statisticsCache.add(t2);

        Statistics statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 2);
        Assert.assertEquals(statistics.getSum().doubleValue(), 30.0, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 15.0, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), 10.0, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), 20.0, 0.01);

        statisticsCache.deleteAll();
        statistics = statisticsCache.mapReduce(x->true);
        Assert.assertEquals(statistics.getCount(), 0);
        Assert.assertEquals(statistics.getSum().doubleValue(), 0.00, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 0.00, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), Double.MAX_VALUE, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), Double.MIN_VALUE, 0.01);
    }

    @Test
    public void add_same_transaction_time() {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();

        int size = 10000;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));

    }

    @Test
    public void add_same_transaction_different_time()  {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();
        int size = 60;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            localDateTime = localDateTime.minusSeconds(1);
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));
    }

    @Test
    public void add_get_transaction()  {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();
        int size = 10000;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.mapReduce(x ->true));
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));
    }


    private Callable<Boolean> getStatisticsSize(StatisticsCache<Transaction, Statistics> statisticsCache, int size, double expectedSum) {
        return () -> statisticsCache.mapReduce(x ->true).getCount() == size
                && statisticsCache.mapReduce(x ->true).getSum().doubleValue() == expectedSum;
    }
}