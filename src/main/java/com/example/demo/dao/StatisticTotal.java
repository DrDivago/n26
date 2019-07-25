package com.example.demo.dao;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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

        Statistics[] statistics = new Statistics[60];
        for (int i = 0 ; i < 60; i++) {
            statistics[i] = statisticsBuckets[i].get();
        }

        for (int i = 0; i < 60; i++) {
            count += statistics[i].getCount();
            sum  = sum.add(statistics[i].getSum());
            avg = avg.add(statistics[i].getAvg());
            if (statistics[i].getMin().compareTo(min) < 0)
                min = statistics[i].getMin();
            if (statistics[i].getMax().compareTo(max) > 0)
                max = statistics[i].getMax();
        }

        return Statistics.Builder.newInstance().
                withSum(sum).
                withAvg(avg).
                withMin(min).
                withMax(max).
                withCount(count)
                .build();
    }

    @Override
    public void addTransaction(Transaction transaction, ValidityRange validityRange) {

        int index = calculateBucket(transaction.getTimestamp().toEpochSecond(ZoneOffset.UTC));


        if (validityRange.checkValid(statisticsBuckets[index].getTimestamp())) {
                statisticsBuckets[index].update(transaction);
        }
        else {
                statisticsBuckets[index].invalidate();
                statisticsBuckets[index].add(transaction);
        }
    }

    @Override
    public void delete() {
        for (int i = 0; i < 60; i++)
            statisticsBuckets[i].invalidate();
    }

    public int calculateBucket(long timestamp) {
        return (int)(timestamp % 60);
    }

    public static void main(String[] args) {

        LocalDateTime a = LocalDateTime.now();
        LocalDateTime b = a.minusSeconds(1);
        System.out.println(a.toEpochSecond(ZoneOffset.UTC));
        System.out.println(b.toEpochSecond(ZoneOffset.UTC));
        long[] stx = new long[60];

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime plusOne = localDateTime.minusSeconds(1);
        LocalDateTime plusTwo = localDateTime.minusSeconds(2);

        System.out.println("now " + localDateTime + " millis " + localDateTime.toInstant(ZoneOffset.UTC).getEpochSecond());
        System.out.println("plus" + plusOne + " plus one: " + plusOne.toInstant(ZoneOffset.UTC).getEpochSecond());
        System.out.println("plus 2" + plusTwo + " plus plusTwo: " + plusTwo.toInstant(ZoneOffset.UTC).getEpochSecond());

        ValidityRange validityRange = new ValidityRange();
        validityRange.updateRange(localDateTime.toEpochSecond(ZoneOffset.UTC));

        LocalDateTime validTrade = localDateTime.minusSeconds(59);

        if (validityRange.checkValid(validTrade.toInstant(ZoneOffset.UTC).getEpochSecond())) {
            System.out.println("Valid");
        }
        else
            System.out.println("Not Valid");

        validTrade = localDateTime.plusSeconds(60);
        validityRange.updateRange(validTrade.toEpochSecond(ZoneOffset.UTC));

        LocalDateTime newTrade = validTrade.minusSeconds(1);

        int index;
        index = ((int)localDateTime.toEpochSecond(ZoneOffset.UTC) % 60) ;
        stx[index] = localDateTime.toEpochSecond(ZoneOffset.UTC);
        System.out.println("index now : " + index);

        index = ((int)plusOne.toEpochSecond(ZoneOffset.UTC) % 60) ;
        stx[index] =plusOne.toEpochSecond(ZoneOffset.UTC);
        System.out.println("plus : " + index);

        index = ((int)plusTwo.toEpochSecond(ZoneOffset.UTC) % 60) ;
        stx[index] = plusTwo.toEpochSecond(ZoneOffset.UTC);
        System.out.println("plus two : " + index);

        index = ((int)newTrade.toEpochSecond(ZoneOffset.UTC) % 60) ;
        //stx[index] = validTrade.toEpochSecond(ZoneOffset.UTC);
        System.out.println("valid : " + index);

        for (int i = 0; i < 60; i++) {
            if (stx[i] != 0) {
                System.out.println("index:" + i + " val " + stx[i]);
            }
        }

        System.out.println(validityRange.checkValid(newTrade.toEpochSecond(ZoneOffset.UTC)));
        System.out.println(validityRange.checkValid(stx[index]));

    }


}
