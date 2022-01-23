package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

public class AccountBalance extends FirebaseDocument {
    private Double balance;
    private String accountId;

    public AccountBalance() {
    }

    public String getAccountId() {
        return accountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
