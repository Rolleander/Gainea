package com.broll.gainea.server.core.map;

public enum AreaType {
    PLAINS("Steppe"),DESERT("Wüste"),LAKE("Meer"),MOUNTAIN("Berg"),BOG("Sumpf"),SNOW("Eisland");
    private String name;

    AreaType(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
