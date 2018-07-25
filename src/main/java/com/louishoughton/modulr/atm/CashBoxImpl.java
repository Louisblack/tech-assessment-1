package com.louishoughton.modulr.atm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.louishoughton.modulr.atm.NotesCalculator.calculateNotesToWithdraw;

public class CashBoxImpl implements CashBox {

    private final Map<Note, Long> cash = new HashMap<>();

    private static Long addNotes(Long a, Long b) {
        return a + b;
    }

    private static long calcTotalForEachNote(Map.Entry<Note, Long> e) {
        return e.getKey().value * e.getValue();
    }

    private static long totalNoteValues(long a, long b) {
        return a + b;
    }

    @Override
    public long checkBalance() {
        return cash.entrySet().stream()
                .mapToLong(CashBoxImpl::calcTotalForEachNote)
                .reduce(0L, CashBoxImpl::totalNoteValues);
    }

    @Override
    public synchronized long replenish(Map<Note, Long> notes) {
        notes.forEach((k, v) -> {
            if (v < 0) {
                throw new IllegalArgumentException("Number of notes must be positive");
            }
            cash.merge(k, v, CashBoxImpl::addNotes);
        });
        return checkBalance();
    }

    @Override
    public synchronized Map<Note, Long> withdraw(long withdrawalInPence) {
        List<Map.Entry<Note, Long>> sortedListOfNotesAndAmounts = getSortedListOfNotesWithAmounts();

        validateWithdrawalIsPossible(withdrawalInPence, sortedListOfNotesAndAmounts);

        Map<Note, Long> notesToWithdraw =
                calculateNotesToWithdraw(withdrawalInPence, sortedListOfNotesAndAmounts, copyOf(cash));

        withdraw(notesToWithdraw);

        return notesToWithdraw;
    }

    private List<Map.Entry<Note, Long>> getSortedListOfNotesWithAmounts() {
        return cash.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((o1, o2) -> o2.getKey().value - o1.getKey().value)
                .collect(Collectors.toList());
    }

    private void validateWithdrawalIsPossible(long withdrawalInPence, List<Map.Entry<Note, Long>> sortedListOfNotesAndAmounts) {
        if (withdrawalInPence % Note.FIVE.value != 0) {
            throw new IllegalArgumentException("withdrawalInPence must be in multiples of 500");
        }
        if (withdrawalInPence > checkBalance()) {
            throw new WithdrawalExceedsBalanceException(String.format("Withdrawal of %s is more than balance %s",
                    withdrawalInPence, checkBalance()));
        }

        Map.Entry<Note, Long> smallestNote = sortedListOfNotesAndAmounts.get(sortedListOfNotesAndAmounts.size() - 1);

        // Can we service the withdrawal with the smallest notes we have?
        if (withdrawalInPence % smallestNote.getKey().value != 0) {
            throw new WithdrawalNotDivisibleByNotesException(String.format("Withdrawal of %s cannot be serviced with notes %s",
                    withdrawalInPence, cash));
        }
    }

    private void withdraw(Map<Note, Long> notesToWithdraw) {
        notesToWithdraw.forEach((note, howManyToWithdraw) -> {
            cash.compute(note, (k2, v2) -> v2 - howManyToWithdraw);
        });
    }
}
