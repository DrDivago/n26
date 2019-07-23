package com.example.demo.dao;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;

public interface StatisticsTotalInterface {

    Statistics getStatistics();

    void addTransaction(Transaction transaction);
}
