package com.louishoughton.modulr.atm;

public enum Note {
    FIVE(500),
    TEN(10000),
    TWENTY(20000),
    FIFTY(500000);

    long value;

    Note(long value) {
        this.value = value;
    }

}
