package com.broll.gainea.core.protocol;

/** Stable values used by the protobuf unit type field. */
public enum UnitType {
    MALE(0),
    FEMALE(1);

    public final int value;

    UnitType(int value) {
        this.value = value;
    }
}
