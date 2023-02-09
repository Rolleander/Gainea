package com.broll.gainea.server.init;

import com.broll.gainea.server.core.map.ExpansionType;

public enum ExpansionSetting {

    BASIC_GAME("Gainea", new ExpansionType[]{ExpansionType.GAINEA}),
    BOG("Eisländer", new ExpansionType[]{ExpansionType.ICELANDS}),
    ICE("Sumpf", new ExpansionType[]{ExpansionType.BOGLANDS}),
    PLUS_ICELANDS("Gainea & Eisländer", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS}),
    PLUS_ICELANDS_AND_BOG("Gainea & Eisländer & Sumpf", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.BOGLANDS}),
    PLUS_BOG("Gainea & Sumpf", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.BOGLANDS}),
    PLUS_ICELANDS_AND_MOUNTAINS("Gainea & Eisländer & Gipfel", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.MOUNTAINS}),
    PLUS_BOG_AND_MOUNTAINS("Gainea & Sumpf & Gipfel", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.BOGLANDS, ExpansionType.MOUNTAINS}),
    BOG_AND_ICE("Eisländer & Sumpf", new ExpansionType[]{ExpansionType.ICELANDS, ExpansionType.BOGLANDS}),
    FULL("Alle", ExpansionType.values());

    private String name;
    private ExpansionType[] maps;

    ExpansionSetting(String name, ExpansionType[] maps) {
        this.name = name;
        this.maps = maps;
    }

    public String getName() {
        return name+ " ("+getMaps().length+" - "+(getMaps().length * 2 +1)+" Spieler)";
    }

    public ExpansionType[] getMaps() {
        return maps;
    }
}
