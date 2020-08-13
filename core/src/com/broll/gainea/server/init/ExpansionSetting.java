package com.broll.gainea.server.init;

public enum ExpansionSetting {

    BASIC_GAME("Basis"),PLUS_ICELANDS("Eisländer"),PLUS_ICELANDS_AND_BOG("Eis & Sumpf"),FULL("Alle Erweiterungen");

    private String name;

    ExpansionSetting(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
