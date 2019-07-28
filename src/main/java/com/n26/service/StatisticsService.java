package com.n26.service;

import com.n26.cache.StatisticsCache;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.util.ValidityRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatisticsService {

    private final StatisticsCache<Transaction, Statistics> statisticsCache;
    private final ValidityRange validityRange = new ValidityRange();

    @Autowired
    public StatisticsService(final StatisticsCache<Transaction, Statistics> statisticsCache) {
        this.statisticsCache = statisticsCache;
    }

    public Statistics getStatistics(LocalDateTime now) {
        validityRange.updateRange(now);
        return statisticsCache.mapReduce( validityRange::isValid);
    }

}
