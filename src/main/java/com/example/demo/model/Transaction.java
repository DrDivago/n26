package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private BigDecimal amount;

    private LocalDateTime timestamp;

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
