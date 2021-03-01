package com.broll.gainea.client.network.sites;

import com.broll.gainea.Gainea;
import com.broll.networklib.client.LobbyClientSite;

public abstract class AbstractGameSite extends LobbyClientSite {

    protected Gainea game;

    public void init(Gainea game){
        this.game =game;
    }

}
