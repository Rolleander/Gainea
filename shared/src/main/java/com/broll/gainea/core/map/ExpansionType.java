package com.broll.gainea.core.map;

public enum ExpansionType {
    GAINEA("Gainea","expansion_0.png"),
    ICELANDS("Eisland","expansion_1.png"),
    BOGLANDS("Sümpfe","expansion_2.png"),
    MOUNTAINS("Gipfel", "expansion_3.png");
    private final String expansionName;
    private final String textureName;

    ExpansionType(String expansionName, String textureName) {
        this.expansionName = expansionName;
        this.textureName = textureName;
    }

    public String getExpansionName() {
        return expansionName;
    }

    public String getTextureName() {
        return textureName;
    }
}
