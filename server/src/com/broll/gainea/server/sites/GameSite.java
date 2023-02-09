package com.broll.gainea.server.sites;

import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.networklib.server.LobbyServerSite;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameSite extends LobbyServerSite<LobbyData, PlayerData> {

    protected GameContainer getGame() {
        return getLobby().getData().getGame();
    }

    protected Player getGamePlayer() {
        return getPlayer().getData().getGamePlayer();
    }

    protected int getPlayersCount() {
        return getGame().getPlayers().size();
    }

    protected Player getCurrentPlayer() {
        GameContainer game = getGame();
        return game.getPlayers().get(game.getCurrentPlayer());
    }

    protected boolean playersTurn() {
        return getGamePlayer().getServerPlayer() == getPlayer() && getGamePlayer().getSkipRounds() <= 0;
    }

    protected void nextTurn() {
        getGame().getReactionHandler().getActionHandlers().getReactionActions().endTurn();
    }
}
