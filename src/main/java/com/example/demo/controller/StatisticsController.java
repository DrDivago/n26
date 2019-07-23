package com.example.demo.controller;

import com.example.demo.model.Statistics;
import com.example.demo.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public Statistics getStatistic(){
        return statisticsService.getStatistics();
    }
}
