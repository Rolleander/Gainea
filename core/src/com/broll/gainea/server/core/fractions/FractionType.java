package com.broll.gainea.server.core.fractions;

public enum FractionType {

    FIRE("Feuerclan"),WATER("Wasserclan");

    private String name;

    FractionType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
