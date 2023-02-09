package com.broll.gainea.server.core.map;

public enum ExpansionType {
    GAINEA("Gainea"),ICELANDS("Eisland"),BOGLANDS("Sümpfe"),MOUNTAINS("Gipfel");
    private String name;

    ExpansionType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
