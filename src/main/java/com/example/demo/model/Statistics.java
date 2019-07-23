package com.example.demo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.math.BigDecimal;

@JsonDeserialize(builder = Statistics.Builder.class)
public class Statistics {

    @JsonPOJOBuilder
    public static class Builder {
        private BigDecimal sum;
        private BigDecimal avg;
        private BigDecimal min;
        private BigDecimal max;
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

    private final BigDecimal sum;
    private final BigDecimal avg;
    private final BigDecimal min;
    private final BigDecimal max;
    private final long count;

    public Statistics(Builder builder) {
        this.sum = builder.sum;
        this.avg = builder.avg;
        this.min = builder.min;
        this.max = builder.max;
        this.count = builder.count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public long getCount() {
        return count;
    }
}
