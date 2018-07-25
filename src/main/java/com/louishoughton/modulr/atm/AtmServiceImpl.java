package com.louishoughton.modulr.atm;

import com.louishoughton.modulr.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AtmServiceImpl implements AtmService {

    private final AccountService accountService;
    private final CashBox cashBox;

    private static final Logger LOG = LoggerFactory.getLogger(AtmServiceImpl.class);

    public AtmServiceImpl(AccountService accountService, CashBox cashBox) {
        this.accountService = accountService;
        this.cashBox = cashBox;
    }

    @Override
    public long replenish(Map<Note, Long> notes) {
        LOG.info("Replenishing ATM with {}", notes);
        return cashBox.replenish(notes);
    }

    @Override
    public String checkBalance(String accountNumber) {
        // We'll ignore internationalisation for now....
        return String.format("%.2f", accountService.checkBalance(accountNumber) / 100F);
    }

    @Override
    public Withdrawal withdraw(String accountNumber, long withdrawalInPence) {
        LOG.debug("Attempting withdrawal of {} from {} with a cash box state of {}", withdrawalInPence, accountNumber, cashBox);
        try {
            return new Withdrawal(attemptWithdrawal(accountNumber, withdrawalInPence));
        } catch (RuntimeException e) {
            LOG.info(String.format("Could not complete withdrawal from account %s", accountNumber), e);
            return new Withdrawal(e.getMessage());
        }
    }

    private synchronized Map<Note, Long> attemptWithdrawal(String accountNumber, long withdrawalInPence) {
        if (accountService.checkBalance(accountNumber) >= withdrawalInPence) {
            //Attempt withdrawal
            Map<Note, Long> withdrawal = cashBox.withdraw(withdrawalInPence);
            //If that hasn't thrown an exception then cash box is good to go
            accountService.withdraw(accountNumber, withdrawalInPence);
            return withdrawal;
        }
        throw new WithdrawalExceedsBalanceException("Not enough funds in account");
    }
}
