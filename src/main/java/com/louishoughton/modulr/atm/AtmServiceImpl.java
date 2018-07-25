package com.louishoughton.modulr.atm;

import com.louishoughton.modulr.account.AccountService;

import java.util.Map;

public class AtmServiceImpl implements AtmService {

    private final AccountService accountService;
    private final CashBox cashBox;

    public AtmServiceImpl(AccountService accountService, CashBox cashBox) {
        this.accountService = accountService;
        this.cashBox = cashBox;
    }

    @Override
    public long replenish(Map<Note, Long> notes) {
        return 0;
    }

    @Override
    public String checkBalance(long accountNumber) {
        return null;
    }

    @Override
    public Map<Note, Long> withdraw(long accountNumber, long withdrawalInPence) {
        return null;
    }
}
