package com.louishoughton.modulr;

import com.google.common.collect.ImmutableMap;
import com.louishoughton.modulr.account.Account;
import com.louishoughton.modulr.account.AccountService;
import com.louishoughton.modulr.account.AccountServiceImpl;
import com.louishoughton.modulr.account.FakeAccountRepository;
import com.louishoughton.modulr.atm.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegrationTest {

    private CashBox cashBox;
    private AccountService accountService;
    private AtmService atmService;

    private List<Account> accounts;

    @Before
    public void setUp() {
        accounts = Arrays.asList(new Account("01001", 273859),
                new Account("01002", 2300),
                new Account("01003", 0));
        cashBox = new CashBoxImpl();
        accountService = new AccountServiceImpl(new FakeAccountRepository(accounts));
        atmService = new AtmServiceImpl(accountService, cashBox);
    }

    @Test
    public void should_show_balance() {
        assertThat(atmService.checkBalance("01001"), equalTo("2738.59"));
    }

    @Test
    public void should_show_zero_balance_with_two_dp() {
        assertThat(atmService.checkBalance("01003"), equalTo("0.00"));
    }

    @Test
    public void should_make_successful_withdrawal_from_account() {
        atmService.replenish(ImmutableMap.of(Note.FIVE, 1L,
                Note.TEN, 4L));

        Withdrawal withdrawal = atmService.withdraw("01001", 1000);

        assertThat(withdrawal.hasError(), is(false));
        assertThat(withdrawal.notes.get(Note.TEN), equalTo(1L));
        assertThat(atmService.checkBalance("01001"), equalTo("2728.59"));
    }

    @Test
    public void should_return_error_if_request_over_account_balance() {
        atmService.replenish(ImmutableMap.of(Note.FIVE, 1L,
                Note.TEN, 4L));

        Withdrawal withdrawal = atmService.withdraw("01003", 1000);

        assertThat(withdrawal.hasError(), is(true));
        assertThat(withdrawal.error, equalTo("Not enough funds in account"));
        assertThat(withdrawal.notes.isEmpty(), is(true));
        assertThat(atmService.checkBalance("01003"), equalTo("0.00"));
    }
}
