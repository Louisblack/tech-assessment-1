package com.louishoughton.modulr.account;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public long checkBalance(String accountNumber) {
        return accountRepository.get(accountNumber).getBalance();
    }

    @Override
    public long withdraw(String accountNumber, long withdrawalInPence) {
        return accountRepository.get(accountNumber).withdraw(withdrawalInPence);
    }
}
