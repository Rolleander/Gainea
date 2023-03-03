package com.broll.gainea.server.sites;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.server.LobbyServerSite;

public abstract class GameSite extends LobbyServerSite<LobbyData, PlayerData> {

    protected GameContainer getGame() {
        return getLobby().getData().getGame();
    }

    protected Player getGamePlayer() {
        return getPlayer().getData().getGamePlayer();
    }

    protected int getPlayersCount() {
        return getGame().getAllPlayers().size();
    }
    
    protected boolean playersTurn() {
        return getGamePlayer() == getGame().getCurrentPlayer() && getGamePlayer().getSkipRounds() <= 0;
    }

    protected void nextTurn() {
        getGame().getReactionHandler().getActionHandlers().getReactionActions().endTurn();
    }
}
