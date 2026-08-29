package com.broll.gainea.core.protocol;

/** Stable values used by the protobuf event screen_effect field. */
public enum ScreenEffect {
    NONE(0),
    HEAL(1),
    DAMAGE(2),
    BUFF(3),
    DEBUFF(4),
    SPAWNED(5),
    MOVED(6);

    public final int value;

    ScreenEffect(int value) {
        this.value = value;
    }
}
