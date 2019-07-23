package com.example.demo.service;

import com.example.demo.TrasanctionNotValidException;
import com.example.demo.TransactionInFutureException;
import com.example.demo.dao.StatisticTotal;
import com.example.demo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


@Service
public class TransactionService {

    private StatisticTotal statisticTotal;

    @Autowired
    public TransactionService(final StatisticTotal statisticTotal) {
        this.statisticTotal = statisticTotal;
    }

    public void addTransaction(Transaction transaction) throws TrasanctionNotValidException, TransactionInFutureException {
        LocalDateTime now = LocalDateTime.now();
        if (isTransactionOlderThenSixtySeconds(transaction, now)) {
            throw new TrasanctionNotValidException();
        }
        if (isTransactionInFuture(transaction, now)) {
            throw new TransactionInFutureException();
        }

        statisticTotal.addTransaction(transaction);

    }

    private boolean isTransactionInFuture(Transaction transaction, LocalDateTime now) {
        return (transaction.getTimestamp().isAfter(now));
    }

    private boolean isTransactionOlderThenSixtySeconds(Transaction transaction, LocalDateTime now) {
        LocalDateTime lessThenSixty = now.minusSeconds(60);
        LocalDateTime transactionTime = transaction.getTimestamp();
        return (transactionTime.isBefore(lessThenSixty));
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String timestamp = formatter.format(now);
        System.out.println(timestamp);
    }

    public void deleteTransactions() {
    }
}
