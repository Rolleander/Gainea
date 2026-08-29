package com.broll.gainea.server.core.objects.monster;

public enum MonsterBehavior {
    RESIDENT("Sesshaft"), RANDOM("Wild"), AGGRESSIVE("Aggressiv"),
    FLEEING("Scheu"), FRIENDLY("Freundlich");

    private final String label;
    MonsterBehavior(String label) { this.label = label; }
    public String getLabel() { return label; }
}
