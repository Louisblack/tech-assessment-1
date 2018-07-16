package com.louishoughton.modulr.account;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AccountServiceImplTest {

    public static final String ACCOUNT_NUMBER = "01001";
    public static final long BALANCE = 3000;

    private AccountServiceImpl service;

    @Before
    public void setUp() {
        service = new AccountServiceImpl(new FakeAccountRepository(Arrays.asList(new Account(ACCOUNT_NUMBER, BALANCE))));
    }

    @Test
    public void should_return_the_correct_balance_from_01001() {
        assertThat(service.checkBalance(ACCOUNT_NUMBER), equalTo(BALANCE));
    }

    @Test
    public void should_withdraw_25_pounds_fifty_from_account() {
        int withdrawalInPence = 2550;
        service.withdraw(ACCOUNT_NUMBER, withdrawalInPence);

        assertThat(service.checkBalance(ACCOUNT_NUMBER), equalTo(BALANCE - withdrawalInPence));
    }
}