package com.louishoughton.modulr.account;

import java.util.concurrent.atomic.AtomicLong;

public final class Account {

    private final String accountNumber;
    private final AtomicLong balance;

    public Account(String accountNumber, long balance) {
        this.accountNumber = accountNumber;
        this.balance = new AtomicLong(balance);
    }

    public long withdraw(long withdrawalInPence) {
        return balance.addAndGet(-withdrawalInPence);
    }

    public long getBalance() {
        return balance.get();
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
