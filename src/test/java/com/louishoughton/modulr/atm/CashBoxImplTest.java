package com.louishoughton.modulr.atm;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CashBoxImplTest {

    private CashBoxImpl cashBox;

    @Before
    public void setUp() throws Exception {
        cashBox = new CashBoxImpl();
    }

    @Test
    public void should_initialise_as_empty() {
        assertThat(cashBox.checkBalance(), equalTo(0L));
    }

    @Test
    public void should_add_twenty_pounds() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY, 1L));
        assertThat(cashBox.checkBalance(), equalTo(2000L));
    }

    @Test
    public void should_add_thirty_five_pounds() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY, 1L,
                                          Note.FIVE,   3L));
        assertThat(cashBox.checkBalance(), equalTo(3500L));
    }

    @Test
    public void should_deal_with_empty_map() {
        cashBox.replenish(new HashMap<>());
        assertThat(cashBox.checkBalance(), equalTo(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_number_of_notes_is_negative() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY, -1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_if_withdrawal_is_not_multiple_of_5_pounds() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY, 1L));
        cashBox.withdraw(275);
    }

    @Test
    public void should_return_empty_optional_if_withdrawal_exceeds_balance() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY, 1L));
        Optional<Map<Note, Long>> withdrawal = cashBox.withdraw(5000);

        assertThat(withdrawal.isPresent(), is(false));
    }

    @Test
    public void should_withdraw_a_single_fifty_note() {
        cashBox.replenish(ImmutableMap.of(Note.FIFTY, 1L));

        Optional<Map<Note, Long>> maybeWithdrawal = cashBox.withdraw(5000);

        assertThat(maybeWithdrawal.isPresent(), is(true));

        Map<Note, Long> withdrawal = maybeWithdrawal.get();
        assertThat(withdrawal.get(Note.FIFTY), is(1L));
    }

    @Test
    public void should_withdraw_70_pounds() {
        cashBox.replenish(ImmutableMap.of(Note.FIFTY, 2L,
                                          Note.TWENTY,1L,
                                          Note.TEN,   2L));

        Optional<Map<Note, Long>> maybeWithdrawal = cashBox.withdraw(7000);

        assertThat(maybeWithdrawal.isPresent(), is(true));

        Map<Note, Long> withdrawal = maybeWithdrawal.get();
        assertThat(withdrawal.get(Note.FIFTY), is(1L));
        assertThat(withdrawal.get(Note.TWENTY), is(1L));
    }

    @Test
    public void should_withdraw_80_pounds() {
        cashBox.replenish(ImmutableMap.of(Note.FIFTY, 2L,
                                          Note.TWENTY,1L,
                                          Note.TEN,   2L));

        Optional<Map<Note, Long>> maybeWithdrawal = cashBox.withdraw(8000);

        assertThat(maybeWithdrawal.isPresent(), is(true));

        Map<Note, Long> withdrawal = maybeWithdrawal.get();
        assertThat(withdrawal.get(Note.FIFTY), is(1L));
        assertThat(withdrawal.get(Note.TWENTY), is(1L));
        assertThat(withdrawal.get(Note.TEN), is(1L));
    }

    @Test
    public void should_withdraw_80_pounds_without_fiftys() {
        cashBox.replenish(ImmutableMap.of(Note.TWENTY,2L,
                                          Note.TEN,   4L));

        Optional<Map<Note, Long>> maybeWithdrawal = cashBox.withdraw(8000);

        assertThat(maybeWithdrawal.isPresent(), is(true));

        Map<Note, Long> withdrawal = maybeWithdrawal.get();
        assertThat(withdrawal.get(Note.TWENTY), is(2L));
        assertThat(withdrawal.get(Note.TEN), is(4L));
    }
}