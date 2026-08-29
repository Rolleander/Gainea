package com.broll.gainea.server.core.map;

public enum AreaType {
    PLAINS("Steppe"), DESERT("Wüste"), LAKE("See"), MOUNTAIN("Berg"), BOG("Sumpf"), SNOW("Eisland");
    private final String typeName;
    AreaType(String typeName) { this.typeName = typeName; }
    public String getTypeName() { return typeName; }
}
