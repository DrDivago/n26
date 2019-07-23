package com.example.demo.service;

import com.example.demo.dao.StatisticTotal;
import com.example.demo.model.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private StatisticTotal statisticTotal;

    @Autowired
    public StatisticsService(final StatisticTotal statisticTotal) {
        this.statisticTotal = statisticTotal;
    }

    public Statistics getStatistics() {
        return statisticTotal.getStatistics();
    }

}
