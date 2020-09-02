package com.broll.gainea.server.init;

import com.broll.gainea.server.core.map.ExpansionType;

public enum ExpansionSetting {

    BASIC_GAME("Basis", new ExpansionType[]{ExpansionType.GAINEA}),
    PLUS_ICELANDS("Eisländer", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS}),
    PLUS_ICELANDS_AND_BOG("Eis & Sumpf", new ExpansionType[]{ExpansionType.GAINEA, ExpansionType.ICELANDS, ExpansionType.BOGLANDS}),
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
