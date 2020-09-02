package com.broll.gainea.client.sites;

import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.GameUI;
import com.broll.networklib.client.LobbyClientSite;

public abstract class AbstractGameSite extends LobbyClientSite {

    protected GameState state;
    protected GameUI gameUI;

    public void init(GameState state, GameUI gameUI){
        this.state = state;
        this.gameUI = gameUI;
    }

}
