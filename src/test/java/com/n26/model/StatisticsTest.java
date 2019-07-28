package com.n26.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class StatisticsTest {

    @Test
    public void get_sum_empty_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();
        Assert.assertEquals(statistics.getSum(), BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assert.assertEquals(statistics.getAvg(), BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assert.assertEquals(statistics.getMin(), BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assert.assertEquals(statistics.getMax(), BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        Assert.assertEquals(statistics.getCount(), 0);
    }

    @Test
    public void update_one_value_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();
        statistics.update(BigDecimal.valueOf(10.4567));

        Assert.assertEquals(statistics.getSum(), BigDecimal.valueOf(10.46));
        Assert.assertEquals(statistics.getAvg(), BigDecimal.valueOf(10.46));
        Assert.assertEquals(statistics.getMin(), BigDecimal.valueOf(10.46));
        Assert.assertEquals(statistics.getMax(), BigDecimal.valueOf(10.46));
        Assert.assertEquals(statistics.getCount(), 1);
    }

    @Test
    public void update_two_value_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();

        statistics.update(BigDecimal.valueOf(10.4476));

        BigDecimal newValue = BigDecimal.valueOf(10.5).setScale(2, BigDecimal.ROUND_HALF_UP);
        statistics.update(newValue);

        Assert.assertEquals(statistics.getSum(), BigDecimal.valueOf(20.95));
        Assert.assertEquals(statistics.getAvg(), BigDecimal.valueOf(10.47));
        Assert.assertEquals(statistics.getMin(), BigDecimal.valueOf(10.45));
        Assert.assertEquals(statistics.getMax().doubleValue(), 10.5, 0.01);
        Assert.assertEquals(statistics.getCount(), 2);
    }
}