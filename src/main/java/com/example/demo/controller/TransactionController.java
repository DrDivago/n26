package com.example.demo.controller;

import com.example.demo.exception.TransactionInFutureException;
import com.example.demo.exception.TransactionNotValidException;
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction transaction){
        try {
            transactionService.addTransaction(transaction, LocalDateTime.now());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TransactionNotValidException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TransactionInFutureException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/transactions")
    public ResponseEntity<?> deleteTransaction(){
        transactionService.deleteTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleException() {
        return HttpStatus.UNPROCESSABLE_ENTITY.toString();
    }
}
