package com.louishoughton.modulr.atm;

import java.util.*;
import java.util.stream.Collectors;

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

        Map<Note, Long> notesToWithdraw = calculateNotesToWithdraw(withdrawalInPence, sortedListOfNotesAndAmounts);

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

    private Map<Note, Long> calculateNotesToWithdraw(long withdrawalInPence, List<Map.Entry<Note, Long>> notesAndNumbers) {
        // If we have no fivers or we're not divisible by 10 so we'll get a fiver anyway or we don't have enough fivers
        // to fill out the next smallest note then use standard method
        if (cash.getOrDefault(Note.FIVE, 0L) == 0L || withdrawalInPence % Note.TEN.value != 0 ||
                !hasEnoughFiversToFillOut(notesAndNumbers)) {
            return calculate(withdrawalInPence, notesAndNumbers);
        } else {
            // We need to ensure we will always get a fiver if possible so just plonk one at the start and fill up the
            // rest with smallest number of notes.
            notesAndNumbers.add(0, new AbstractMap.SimpleEntry<>(Note.FIVE, 1L));
            return calculate(withdrawalInPence, notesAndNumbers);
        }
    }

    private boolean hasEnoughFiversToFillOut(List<Map.Entry<Note, Long>> notesAndNumbers) {
        Optional<Map.Entry<Note, Long>> smallestNonFiveNote = getSmallestNonFiveNote(notesAndNumbers);
        return smallestNonFiveNote.isPresent() &&
                cash.getOrDefault(Note.FIVE, 0L) >= smallestNonFiveNote.get().getKey().value / Note.FIVE.value;
    }

    private Optional<Map.Entry<Note, Long>> getSmallestNonFiveNote(List<Map.Entry<Note, Long>> notesAndNumbers) {
        List<Map.Entry<Note, Long>> nonFIveNotes = notesAndNumbers.stream().filter(n -> n.getKey() != Note.FIVE).collect(Collectors.toList());
        return nonFIveNotes.isEmpty() ? Optional.empty() : Optional.of(nonFIveNotes.get(nonFIveNotes.size() - 1));
    }

    private static Map<Note, Long> calculate(long withdrawalInPence, List<Map.Entry<Note, Long>> notesAndNumbers) {
        if (notesAndNumbers.isEmpty()) {
            return new HashMap<>();
        }
        Map.Entry<Note, Long> largestNote = notesAndNumbers.get(0);
        long howManyNoteDoINeed = withdrawalInPence / largestNote.getKey().value;
        long howManyNotesDoIHave = largestNote.getValue();
        long howManyToWithdraw = howManyNoteDoINeed <= howManyNotesDoIHave ? howManyNoteDoINeed : howManyNotesDoIHave;
        long howMuchLeftToWithdraw = withdrawalInPence - (largestNote.getKey().value * howManyToWithdraw);

        Map<Note, Long> howMuchOfThisNote = new HashMap<>();
        if (howManyToWithdraw > 0) {
            howMuchOfThisNote.put(largestNote.getKey(), howManyToWithdraw);
        }
        return combineMaps(howMuchOfThisNote,
                calculate(howMuchLeftToWithdraw, notesAndNumbers.subList(1, notesAndNumbers.size())));
    }

    private static Map<Note, Long> combineMaps(Map<Note, Long> a, Map<Note, Long> b) {
        HashMap<Note, Long> combined = new HashMap<>();
        a.forEach((k,v) -> {
            combined.merge(k, v, CashBoxImpl::addNotes);
        });
        b.forEach((k,v) -> {
            combined.merge(k, v, CashBoxImpl::addNotes);
        });
        return combined;
    }

    private void withdraw(Map<Note, Long> notesToWithdraw) {
        notesToWithdraw.forEach((note, howManyToWithdraw) -> {
            cash.compute(note, (k2, v2) -> v2 - howManyToWithdraw);
        });
    }
}
