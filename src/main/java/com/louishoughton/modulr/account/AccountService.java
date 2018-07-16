package com.louishoughton.modulr.account;

/**
 * A service to model actions on bank accounts
 */
public interface AccountService {

    /**
     * Check the balance of the account with the account number provided
     * @param accountNumber The unique account number
     * @return The balance in pence
     */
    long checkBalance(long accountNumber);

    /**
     * Makes a withdrawal from the account with the account number provided.
     * @param accountNumber The unique account number
     * @param withdrawalInPence The amount in pence to withdraw
     * @return The new balance in pence after the withdrawal
     */
    long withdraw(long accountNumber, long withdrawalInPence);
}
