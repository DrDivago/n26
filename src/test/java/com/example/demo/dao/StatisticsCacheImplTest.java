package com.example.demo.dao;

import com.example.demo.cache.StatisticsCache;
import com.example.demo.cache.StatisticsCacheImpl;
import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
}