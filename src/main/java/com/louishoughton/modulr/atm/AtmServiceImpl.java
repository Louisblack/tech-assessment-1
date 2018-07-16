package com.louishoughton.modulr.atm;

import java.util.Map;

public class AtmServiceImpl implements AtmService {
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
