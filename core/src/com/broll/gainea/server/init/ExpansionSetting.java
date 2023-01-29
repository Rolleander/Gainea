package com.broll.gainea.server.init;

import com.broll.gainea.server.core.map.ExpansionType;

public enum ExpansionSetting {

    BASIC_GAME("Basis", new ExpansionType[]{ExpansionType.GAINEA}),
    PLUS_ICELANDS("Eisländer", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS}),
    PLUS_ICELANDS_AND_BOG("Eis & Sumpf", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.BOGLANDS}),
    PLUS_BOG("Sumpf", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.BOGLANDS}),
    PLUS_ICELANDS_AND_MOUNTAINS("Eisländer & Gipfel", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.MOUNTAINS}),
    PLUS_BOG_AND_MOUNTAINS("Sumpf & Gipfel", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.BOGLANDS, ExpansionType.MOUNTAINS}),
    FULL("Alle Erweiterungen", ExpansionType.values());

    private String name;
    private ExpansionType[] maps;

    ExpansionSetting(String name, ExpansionType[] maps) {
        this.name = name;
        this.maps = maps;
    }

    public String getName() {
        return name;
    }

    public ExpansionType[] getMaps() {
        return maps;
    }
}
