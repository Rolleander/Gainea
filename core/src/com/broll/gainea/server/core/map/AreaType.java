package com.broll.gainea.server.core.map;

public enum AreaType {
    PLAINS("Grassland"),DESERT("Desert"),LAKE("Lake"),MOUNTAIN("Mountain"),BOG("Bog"),SNOW("Snow");
    private String name;

    AreaType(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
