package com.deepak.codechallenge.domain;


import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TransactionDTO {

    //For some reason Lombok not generating getters and setters and hence setting explicitly

    private BigDecimal amount;

    private ZonedDateTime timeStamp;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(ZonedDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "amount=" + amount +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
