package com.louishoughton.modulr.atm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public synchronized Optional<Map<Note, Long>> withdraw(long withdrawalInPence) {
        if (withdrawalInPence % 500 != 0) {
            throw new IllegalArgumentException("withdrawalInPence must be in multiples of 500");
        }
        if (withdrawalInPence > checkBalance()) {
            return Optional.empty();
        }

        List<Map.Entry<Note, Long>> sortedListOfNotesAndAmounts = cash.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .sorted((o1, o2) -> o2.getKey().value - o1.getKey().value)
                .collect(Collectors.toList());

        Map.Entry<Note, Long> smallestNote = sortedListOfNotesAndAmounts.get(sortedListOfNotesAndAmounts.size() - 1);

        // Can we service the withdrawal with the smallest notes we have?
        if (withdrawalInPence % smallestNote.getKey().value != 0) {
            return Optional.empty();
        }

        Map<Note, Long> notesToWithdraw = calculateNotesToWithdraw(withdrawalInPence, sortedListOfNotesAndAmounts);

        notesToWithdraw.forEach((note, howManyToWithdraw) -> {
            cash.compute(note, (k2, v2) -> v2 - howManyToWithdraw);
        });

        return Optional.of(notesToWithdraw);
    }

    private static Map<Note, Long> calculateNotesToWithdraw(long withdrawalInPence, List<Map.Entry<Note, Long>> notesAndNumbers) {
        if (notesAndNumbers.isEmpty()) {
            return new HashMap<>();
        }
        Map.Entry<Note, Long> largestNote = notesAndNumbers.get(0);
        long howManyNoteDoINeed = withdrawalInPence / largestNote.getKey().value;
        long howManyNotesDoIHave = largestNote.getValue();
        long howManyToWithdraw = howManyNoteDoINeed <= howManyNotesDoIHave ? howManyNoteDoINeed : howManyNotesDoIHave;
        long howMuchLeftToWithdraw = withdrawalInPence - (largestNote.getKey().value * howManyToWithdraw);

        Map<Note, Long> howMuchOfThisNote = new HashMap<>();
        howMuchOfThisNote.put(largestNote.getKey(), howManyToWithdraw);
        return combineMaps(howMuchOfThisNote,
                calculateNotesToWithdraw(howMuchLeftToWithdraw, notesAndNumbers.subList(1, notesAndNumbers.size())));
    }

    private static Map<Note, Long> combineMaps(Map<Note, Long> a, Map<Note, Long> b) {
        a.putAll(b);
        return a;
    }
}
