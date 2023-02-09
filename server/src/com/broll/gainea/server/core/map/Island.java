package com.broll.gainea.server.core.map;

public class Island extends AreaCollection{

    private IslandID id;

    public Island(IslandID id){
        this.id = id;
    }

    public IslandID getId() {
        return id;
    }
}
