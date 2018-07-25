package com.louishoughton.modulr.atm;

import java.util.*;
import java.util.stream.Collectors;

class NotesCalculator {

    static Map<Note, Long> calculateNotesToWithdraw(long withdrawalInPence, List<Map.Entry<Note, Long>> notesAndNumbers, Map<Note, Long> cash) {
        // If we have no fivers or we're not divisible by 10 so we'll get a fiver anyway or we don't have enough fivers
        // to fill out the next smallest note then just calculate as normal
        if (cash.getOrDefault(Note.FIVE, 0L) == 0L || withdrawalInPence % Note.TEN.value != 0 ||
                !hasEnoughFiversToFillOut(notesAndNumbers, cash)) {
            return calculate(withdrawalInPence, notesAndNumbers);
        } else {
            // We need to ensure we will always get a fiver if possible so just plonk one at the start and fill up the
            // rest with smallest number of notes. We've already checked that we have enough to cover it.
            notesAndNumbers.add(0, new AbstractMap.SimpleEntry<>(Note.FIVE, 1L));
            return calculate(withdrawalInPence, notesAndNumbers);
        }
    }

    private static boolean hasEnoughFiversToFillOut(List<Map.Entry<Note, Long>> notesAndNumbers, Map<Note, Long> cash) {
        Optional<Map.Entry<Note, Long>> smallestNonFiveNote = getSmallestNonFiveNote(notesAndNumbers);
        return smallestNonFiveNote.isPresent() &&
                cash.getOrDefault(Note.FIVE, 0L) >= smallestNonFiveNote.get().getKey().value / Note.FIVE.value;
    }

    private static Optional<Map.Entry<Note, Long>> getSmallestNonFiveNote(List<Map.Entry<Note, Long>> notesAndNumbers) {
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
            combined.merge(k, v, NotesCalculator::addNotes);
        });
        b.forEach((k,v) -> {
            combined.merge(k, v, NotesCalculator::addNotes);
        });
        return combined;
    }

    private static Long addNotes(Long a, Long b) {
        return a + b;
    }
}
