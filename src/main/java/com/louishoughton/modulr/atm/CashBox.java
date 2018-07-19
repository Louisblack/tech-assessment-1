package com.louishoughton.modulr.atm;

import java.util.Map;
import java.util.Optional;

public interface CashBox {

    /**
     * Shows the balance of the ATM cash box.
     *
     * @return The balance of the ATM's cash box in pence
     */
    long checkBalance();

    /**
     * Replenish the atm cash box with the provided number of notes in specific denominations
     *
     * @param notes A Map of Notes and number of each note
     * @return The new balance in pence that the ATM cash box now holds
     */
    long replenish(Map<Note, Long> notes);

    /**
     * Makes a withdrawal from the account with the account number provided.
     *
     * @param withdrawalInPence The amount to withdraw in pence
     * @return A Map of the notes and number of each note
     * of each notes for the withdrawal
     */
    Map<Note, Long> withdraw(long withdrawalInPence);
}
