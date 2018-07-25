package com.louishoughton.modulr.atm;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Withdrawal {
    public final Map<Note, Long> notes;
    public final String error;

    public Withdrawal(Map<Note, Long> notes) {
        this.notes = ImmutableMap.copyOf(notes);
        this.error = "";
    }

    public Withdrawal(String error) {
        this.error = error;
        this.notes = ImmutableMap.of();
    }

    public boolean hasError() {
        return error.length() > 0;
    }
}
