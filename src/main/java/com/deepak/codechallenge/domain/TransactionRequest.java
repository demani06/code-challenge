package com.deepak.codechallenge.domain;

import javax.validation.constraints.NotNull;

public class TransactionRequest {
    @NotNull
    private String amount;
    @NotNull
    private String timeStamp;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
