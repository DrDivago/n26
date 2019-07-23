package com.example.demo.dao;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StatisticTotal implements StatisticsTotalInterface {

    private StatisticsBucket[] statisticsBuckets;

    public StatisticTotal() {
        statisticsBuckets = new StatisticsBucket[60];
        for (int i = 0; i < 60; i++) {
            statisticsBuckets[i] = new StatisticsBucket();
        }
    }

    public Statistics getStatistics() {
        BigDecimal sum = new BigDecimal(0.0);
        BigDecimal avg = new BigDecimal(0.0);
        BigDecimal min = BigDecimal.valueOf(Double.MAX_VALUE);
        BigDecimal max = BigDecimal.valueOf(Double.MIN_VALUE);
        long count = 0;
        for (int i = 0 ; i < 60; i++) {
            count += statisticsBuckets[i].getCount();
            sum  = sum.add(statisticsBuckets[i].getSum());
            avg = avg.add(statisticsBuckets[i].getAvg());
            if (statisticsBuckets[i].getMin().compareTo(min) < 0)
                min = statisticsBuckets[i].getMin();
            if (statisticsBuckets[i].getMax().compareTo(max) > 0)
                max = statisticsBuckets[i].getMax();
        }

        Statistics statistics = Statistics.Builder.newInstance().
                withSum(sum).
                withAvg(avg).
                withMin(min).
                withMax(max).
                withCount(count)
                .build();

        return statistics;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        statisticsBuckets[0].addTransaction(transaction);
    }

    public int calculateBucket(long timestamp) {
        return 0;
    }
}
