package com.louishoughton.modulr.atm;

public enum Note {
    FIVE(500),
    TEN(1000),
    TWENTY(2000),
    FIFTY(5000);

    int value;

    Note(int value) {
        this.value = value;
    }

}
