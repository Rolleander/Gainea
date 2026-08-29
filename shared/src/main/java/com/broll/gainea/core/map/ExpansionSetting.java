package com.broll.gainea.core.map;

public enum ExpansionSetting {
    BASIC_GAME("Gainea", ExpansionType.GAINEA),
    BOG("Eisländer", ExpansionType.ICELANDS),
    ICE("Sumpf", ExpansionType.BOGLANDS),
    PLUS_ICELANDS("Gainea & Eisländer", ExpansionType.GAINEA, ExpansionType.ICELANDS),
    PLUS_ICELANDS_AND_BOG("Gainea & Eisländer & Sumpf", ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.BOGLANDS),
    PLUS_BOG("Gainea & Sumpf", ExpansionType.GAINEA, ExpansionType.BOGLANDS),
    PLUS_ICELANDS_AND_MOUNTAINS("Gainea & Eisländer & Gipfel", ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.MOUNTAINS),
    PLUS_BOG_AND_MOUNTAINS("Gainea & Sumpf & Gipfel", ExpansionType.GAINEA, ExpansionType.BOGLANDS, ExpansionType.MOUNTAINS),
    BOG_AND_ICE("Eisländer & Sumpf", ExpansionType.ICELANDS, ExpansionType.BOGLANDS),
    FULL("Alle", ExpansionType.values());

    private final String expansionName;
    private final ExpansionType[] maps;

    ExpansionSetting(String expansionName, ExpansionType... maps) {
        this.expansionName = expansionName;
        this.maps = maps;
    }

    public ExpansionType[] getMaps() {
        return maps;
    }

    public String getName() {
        return expansionName + " (" + maps.length + " - " + (maps.length * 2 + 1) + " Spieler)";
    }
}
