package com.louishoughton.modulr.atm;

public class WithdrawalExceedsBalanceException extends RuntimeException {
    public WithdrawalExceedsBalanceException(String message) {
        super(message);
    }
}
