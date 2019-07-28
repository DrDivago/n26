package com.example.demo.service;

import com.example.demo.cache.StatisticsCache;
import com.example.demo.exception.TransactionNotValidException;
import com.example.demo.exception.TransactionInFutureException;
import com.example.demo.model.Statistics;
import com.example.demo.util.ValidityRange;
import com.example.demo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class TransactionService {

    private final StatisticsCache<Transaction, Statistics> statisticsCache;
    private final ValidityRange validityRange = new ValidityRange();

    @Autowired
    public TransactionService(final StatisticsCache<Transaction, Statistics> statisticsCache) {
        this.statisticsCache = statisticsCache;
    }

    public void addTransaction(Transaction transaction, LocalDateTime now ) throws TransactionNotValidException, TransactionInFutureException {
        if (isTransactionOlderThenSixtySeconds(transaction, now)) {
            throw new TransactionNotValidException();
        }
        if (isTransactionInFuture(transaction, now)) {
            throw new TransactionInFutureException();
        }

        validityRange.updateRange(now.toEpochSecond(ZoneOffset.UTC));
        if (validityRange.isValid((statisticsCache.getTimestamp(transaction)))) {
            statisticsCache.update(transaction);
        }
        else {
            statisticsCache.invalidate(transaction);
            statisticsCache.add(transaction);
        }
    }

    private boolean isTransactionInFuture(Transaction transaction, LocalDateTime now) {
        return (transaction.getTimestamp().isAfter(now));
    }

    private boolean isTransactionOlderThenSixtySeconds(Transaction transaction, LocalDateTime now) {
        LocalDateTime lessThenSixty = now.minusSeconds(60);
        LocalDateTime transactionTime = transaction.getTimestamp();
        return (transactionTime.isBefore(lessThenSixty));
    }

    public void deleteTransactions() {
        statisticsCache.deleteAll();
    }
}
