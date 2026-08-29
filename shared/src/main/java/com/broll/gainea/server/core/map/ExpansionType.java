package com.broll.gainea.server.core.map;

public enum ExpansionType {
    GAINEA("Gainea"), ICELANDS("Eisland"), BOGLANDS("Sümpfe"), MOUNTAINS("Gipfel");
    private final String expansionName;
    ExpansionType(String expansionName) { this.expansionName = expansionName; }
    public String getExpansionName() { return expansionName; }
}
