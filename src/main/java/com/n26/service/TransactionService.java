package com.n26.service;

import com.n26.cache.StatisticsCache;
import com.n26.exception.TransactionInFutureException;
import com.n26.exception.TransactionNotValidException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.util.ValidityRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


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

        validityRange.updateRange(now);
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
