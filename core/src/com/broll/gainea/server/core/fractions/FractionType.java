package com.broll.gainea.server.core.fractions;

public enum FractionType {

    FIRE("Feuerclan"), WATER("Wasserclan"), PLANT("Pflanzenclan"), LIGHNTNING("Blitzclan"), SKELETON("Skelette"), VIKINGS("Wikinger"),
    NOMADS("Nomaden"), KNIGHTS("Ritter"), SAMURAI("Samurai"), MONKS("Mönche");

    private String name;

    FractionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
