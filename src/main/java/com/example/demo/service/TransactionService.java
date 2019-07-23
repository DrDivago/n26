package com.example.demo.service;

import com.example.demo.TrasanctionNotValidException;
import com.example.demo.TransactionInFutureException;
import com.example.demo.dao.StatisticTotal;
import com.example.demo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class TransactionService {

    private StatisticTotal statisticTotal;

    @Autowired
    public TransactionService(final StatisticTotal statisticTotal) {
        this.statisticTotal = statisticTotal;
    }

    public void addTransaction(Transaction transaction) throws TrasanctionNotValidException, TransactionInFutureException {
        if (isTransactionOlderThenSixtySeconds(transaction)) {
            throw new TrasanctionNotValidException();
        }
        if (isTransactionInFuture(transaction)) {
            throw new TransactionInFutureException();
        }

        statisticTotal.addTransaction(transaction);

    }

    private boolean isTransactionInFuture(Transaction transaction) {
        return (transaction.getTimestamp().isAfter(LocalDateTime.now()));
    }

    private boolean isTransactionOlderThenSixtySeconds(Transaction transaction) {
        return (transaction.getTimestamp().isBefore(LocalDateTime.now().minusSeconds(60)));
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lessSixty = now.minusSeconds(60);
        LocalDateTime current = now.minusSeconds(61);
        System.out.println(current.isAfter(lessSixty) || current.isEqual(lessSixty));

    }
}
