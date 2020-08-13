package com.broll.gainea.server.sites;

import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.net.NT_PlayerWait;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.GameContainer;
import com.broll.networklib.server.LobbyServerSite;

public abstract class AbstractGameSite extends LobbyServerSite<LobbyData, PlayerData> {


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

    public void doPlayerTurn(Player player, NT_PlayerTurn turn) {
        //reset actions
        getGame().clearActions();
        //send turn to player
        player.getServerPlayer().sendTCP(turn);
        NT_PlayerWait wait = new NT_PlayerWait();
        wait.playersTurn = player.getServerPlayer().getId();
        //send wait to all others
        getGame().getPlayers().forEach(p -> p.getServerPlayer().sendTCP(wait));
    }

}
