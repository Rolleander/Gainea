package com.broll.gainea.server.core.map;

public enum ExpansionType {
    GAINEA("Gainea"),ICELANDS("Icelands"),BOGLANDS("Boglands"),MOUNTAINS("Mountains");
    private String name;

    ExpansionType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
