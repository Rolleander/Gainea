package com.broll.gainea.server.core.map;

public class Continent extends AreaCollection{

    private ContinentID id;

    public Continent(ContinentID id){
        this.id =id;
    }

    public ContinentID getId() {
        return id;
    }
}
