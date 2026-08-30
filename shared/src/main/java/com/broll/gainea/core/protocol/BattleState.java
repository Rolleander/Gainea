package com.broll.gainea.core.protocol;

/** Stable values used by the protobuf battle-update state field. */
public enum BattleState {
    FIGHTING(0),
    ATTACKER_WON(1),
    DEFENDER_WON(2),
    DRAW(3);

    public final int value;

    BattleState(int value) {
        this.value = value;
    }
}
