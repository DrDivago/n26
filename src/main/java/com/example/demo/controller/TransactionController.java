package com.example.demo.controller;

import com.example.demo.TransactionInFutureException;
import com.example.demo.TrasanctionNotValidException;
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@Valid @RequestBody Transaction transaction){
        try {
            transactionService.addTransaction(transaction);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (TrasanctionNotValidException e) {
            System.out.println(e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TransactionInFutureException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }
}
