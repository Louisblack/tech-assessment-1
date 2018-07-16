package com.louishoughton.modulr.account;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeAccountRepository implements AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public FakeAccountRepository(List<Account> accounts) {
        accounts.forEach(a -> {
            this.accounts.put(a.getAccountNumber(), a);
        });
    }

    @Override
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }
}
