package com.broll.gainea.server.init;

import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_PlayerSettings;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.networklib.server.impl.LobbySettings;

public class PlayerData implements LobbySettings {

    private FractionType fraction;

    private Player gamePlayer;

    private boolean ready = false;

    public void joinedGame(Player gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setFraction(FractionType fraction) {
        this.fraction = fraction;
    }

    public FractionType getFraction() {
        return fraction;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    public Player getGamePlayer() {
        return gamePlayer;
    }

    @Override
    public NT_PlayerSettings getSettings() {
        NT_PlayerSettings playerSettings = new NT_PlayerSettings();
        playerSettings.fraction = fraction.ordinal();
        playerSettings.ready = ready;
        return playerSettings;
    }
}
