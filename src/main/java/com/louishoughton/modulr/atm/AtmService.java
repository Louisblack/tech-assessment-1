package com.louishoughton.modulr.atm;

import java.util.Map;

/**
 * A service to model an ATM.
 */
public interface AtmService {

    /**
     * Replenish the atm with the provided number of notes in specific denominations
     *
     * @param notes A Map of Notes and number of each note
     * @return The new balance in pence that the ATM now holds
     */
    long replenish(Map<Note, Long> notes);

    /**
     * Shows the account number formatted as a string
     *
     * @param accountNumber The unique account number of the account for which to check the balance
     * @return The balance of the account formatted as a string
     */
    String checkBalance(String accountNumber);

    /**
     * Makes a withdrawal from the account with the account number provided.
     *
     * @param accountNumber     The unique of the account for which to make the withdrawal
     * @param withdrawalInPence The withdrawal amount to make in pence
     * @return A Map of notes and the number of each note withdrawn
     */
    Withdrawal withdraw(String accountNumber, long withdrawalInPence);
}
