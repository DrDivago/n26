package com.example.demo.service;

import com.example.demo.cache.StatisticsCache;
import com.example.demo.util.ValidityRange;
import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class StatisticsService {

    private final StatisticsCache<Transaction, Statistics> statisticsCache;
    private final ValidityRange validityRange = new ValidityRange();

    @Autowired
    public StatisticsService(final StatisticsCache<Transaction, Statistics> statisticsCache) {
        this.statisticsCache = statisticsCache;
    }

    public Statistics getStatistics(LocalDateTime now) {
        validityRange.updateRange(now.toEpochSecond(ZoneOffset.UTC));
        return statisticsCache.mapReduce(validityRange::isValid);
    }

}
