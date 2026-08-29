package com.broll.gainea.core.cards;

public enum EffectType {
    BUFF("Buff"), DEBUFF("Debuff"), MOVEMENT("Transportation"),
    DISRUPTION("Manipulation"), CHAOS("Chaos"), SUMMON("Beschwörung"), OTHER("Andere");

    private final String displayName;

    EffectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
