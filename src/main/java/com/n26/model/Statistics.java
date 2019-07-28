package com.n26.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.math.BigDecimal;

@JsonDeserialize(builder = Statistics.Builder.class)
public class Statistics {

    @JsonPOJOBuilder
    public static class Builder {
        private BigDecimal sum = BigDecimal.ZERO;
        private BigDecimal avg = BigDecimal.ZERO;
        private BigDecimal min = BigDecimal.ZERO;
        private BigDecimal max = BigDecimal.ZERO;
        private long count;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() { }

        public Builder withSum(BigDecimal sum) {
            this.sum = sum;
            return this;
        }

        public Builder withAvg(BigDecimal avg) {
            this.avg = avg;
            return this;
        }

        public Builder withMin(BigDecimal min) {
            this.min = min;
            return this;
        }

        public Builder withMax(BigDecimal max) {
            this.max = max;
            return this;
        }

        public Builder withCount(long count) {
            this.count = count;
            return this;
        }

        public Statistics build() {
            return new Statistics(this);
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal sum;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal avg;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal max;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal min;
    private long count;

    private Statistics(Builder builder) {
        this.sum = builder.sum;
        this.avg = builder.avg;
        this.max = builder.max;
        this.min = builder.min;
        this.count = builder.count;
    }

    public BigDecimal getSum() {
        return sum.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getAvg() {
        return avg.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMin() {
        return min.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMax() {
        return max.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public long getCount() {
        return count;
    }


    /**
     * Update the statistics with a new value
     * @param value value used to update the statistics
     */
    public void update(BigDecimal value) {
        incrementCount(1);
        updateSum(value);
        updateAvg();
        updateMin(value);
        updateMax(value);
    }

    /**
     * Merge the statistics of newStatistics in the current statistics
     * @param newStatistic statistics to be merged in current statistics
     */
    public void merge(Statistics newStatistic) {
        incrementCount(newStatistic.getCount());
        updateSum(newStatistic.getSum());
        updateAvg();
        updateMin(newStatistic.getMin());
        updateMax(newStatistic.getMax());
    }

    private void incrementCount(long value) {
        this.count += value;
    }

    private void updateSum(BigDecimal value) {
        sum = sum.add(value);
    }

    private void updateAvg() {
        avg = sum.divide(BigDecimal.valueOf(getCount()), BigDecimal.ROUND_HALF_UP);
    }

    private void updateMin(BigDecimal amount) {
        if (min.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(min) < 0) {
            this.min = amount;
        }
    }

    private void updateMax(BigDecimal amount) {
        if (max.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(max) > 0) {
            this.max = amount;
        }
    }
}
