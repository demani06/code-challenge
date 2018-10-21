package com.deepak.codechallenge.domain;

import javax.validation.constraints.NotNull;

public class TransactionRequest {
    @NotNull
    private String amount;
    @NotNull
    private String timestamp;

    public TransactionRequest() {
    }

    public TransactionRequest(@NotNull String amount, @NotNull String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
