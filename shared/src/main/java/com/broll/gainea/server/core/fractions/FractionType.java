package com.broll.gainea.server.core.fractions;

public enum FractionType {
    SHADOW("Schatten"), BARBARIANS("Barbaren"), VIKINGS("Wikinger"), DRUIDS("Druiden"),
    KNIGHTS("Kreuzritter"), SAMURAI("Samurai"), MONKS("Mönche"), POACHER("Wilderer"),
    RANGER("Waldläufer"), MERCENARY("Söldner"), GUARDS("Königsgarde"), FIRE("Feuermagier"),
    WATER("Wassermagier"), SPARTANS("Spartaner"), LIZARDS("Echsenvolk");

    private final String displayName;
    FractionType(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}
