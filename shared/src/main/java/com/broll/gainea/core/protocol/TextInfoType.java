package com.broll.gainea.core.protocol;

/** Stable values used by the protobuf text-info type field. */
public enum TextInfoType {
    LOG(0),
    DISPLAY(1),
    CONFIRM(2);

    public final int value;

    TextInfoType(int value) {
        this.value = value;
    }
}
