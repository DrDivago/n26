package com.example.demo.dao;

import com.example.demo.model.Transaction;

import java.math.BigDecimal;

public class StatisticsBucket {

    private BigDecimal sum;
    private BigDecimal avg;
    private long count;
    private BigDecimal min;
    private BigDecimal max;

    public StatisticsBucket() {
        this.sum = BigDecimal.ZERO;
        this.avg = BigDecimal.ZERO;
        this.min = BigDecimal.valueOf(Double.MAX_VALUE);
        this.max = BigDecimal.valueOf(Double.MIN_VALUE);
    }
    public void addTransaction(Transaction transaction) {
        count += 1;
        sum = sum.add(transaction.getAmount());
        avg = sum.divide(new BigDecimal(count), BigDecimal.ROUND_UP);
        if (transaction.getAmount().compareTo(min) < 0) {
            min = transaction.getAmount();
        }

        if (transaction.getAmount().compareTo(max) > 0) {
            max = transaction.getAmount();
        }
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }
}
