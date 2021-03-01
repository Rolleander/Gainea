package com.broll.gainea.server.core.fractions;

public enum FractionType {

    SHADOW("Schatten"),BARBARIANS("Barbaren"), VIKINGS("Wikinger"),
    DRUIDS("Druiden"), KNIGHTS("Kreuzritter"), SAMURAI("Samurai"), MONKS("Mönche"),
    POACHER("Wilderer"),RANGER("Waldläufer"),MERCENARY("Söldner"),GUARDS("Königsgarde"),
    FIRE("Feuermagier"),  WATER("Wassermagier"), LIZARDS("Echsenvolk");

    private String name;

    FractionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
