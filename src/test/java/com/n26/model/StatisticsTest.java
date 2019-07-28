package com.n26.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class StatisticsTest {

    @Test
    public void get_sum_empty_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();
        Assert.assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getSum());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getAvg());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMin());
        Assert.assertEquals(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMax());
        Assert.assertEquals(0, statistics.getCount());
    }

    @Test
    public void update_one_value_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();
        statistics.update(BigDecimal.valueOf(10.4567));

        Assert.assertEquals(BigDecimal.valueOf(10.46).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getSum());
        Assert.assertEquals(BigDecimal.valueOf(10.46).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(10.46).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMin());
        Assert.assertEquals(BigDecimal.valueOf(10.46).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMax());
        Assert.assertEquals(1, statistics.getCount());
    }

    @Test
    public void update_two_value_statistics() {
        Statistics statistics = Statistics.Builder.newInstance().build();

        statistics.update(BigDecimal.valueOf(10.4476));

        BigDecimal newValue = BigDecimal.valueOf(10.5).setScale(2, BigDecimal.ROUND_HALF_UP);
        statistics.update(newValue);

        Assert.assertEquals(BigDecimal.valueOf(20.95).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getSum());
        Assert.assertEquals(BigDecimal.valueOf(10.47).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getAvg());
        Assert.assertEquals(BigDecimal.valueOf(10.45).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMin());
        Assert.assertEquals(BigDecimal.valueOf(10.5).setScale(2, BigDecimal.ROUND_HALF_UP), statistics.getMax());
        Assert.assertEquals(2, statistics.getCount());
    }
}