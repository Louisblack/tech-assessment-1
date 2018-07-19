package com.louishoughton.modulr.atm;

public class WithdrawalNotDivisibleByNotesException extends RuntimeException {
    public WithdrawalNotDivisibleByNotesException(String message) {
        super(message);
    }
}
