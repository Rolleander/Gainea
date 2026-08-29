package com.broll.gainea.core.protocol;

/** Stable values used by the protobuf board-effect effect field. */
public enum BoardEffectType {
    FIRE(0),
    PORTAL(1);

    public final int value;

    BoardEffectType(int value) {
        this.value = value;
    }
}
