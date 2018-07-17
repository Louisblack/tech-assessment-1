package com.louishoughton.modulr.account;

public interface AccountRepository {

    /**
     * Retrieves the requested account from the repository
     *
     * @param accountNumber The unique account number for the requested account
     * @return The Account with the unique account number provided
     */
    Account get(String accountNumber);
}
