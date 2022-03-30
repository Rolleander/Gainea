package com.broll.gainea.client.network;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.client.ui.screens.StartScreen;
import com.broll.networklib.client.auth.LastConnection;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.ILobbyUpdateListener;
import com.broll.networklib.client.impl.LobbyPlayer;

public class ClientLobbyListener implements ILobbyUpdateListener {

    private Gainea game;

    public ClientLobbyListener(Gainea game) {
        this.game = game;
    }

    @Override
    public void lobbyUpdated(GameLobby lobby) {
        updateLobby();
    }

    @Override
    public void playerJoined(GameLobby lobby, LobbyPlayer player) {
        updateLobby();
    }

    @Override
    public void playerLeft(GameLobby lobby, LobbyPlayer player) {
        updateLobby();
    }

    @Override
    public void kickedFromLobby(GameLobby lobby) {
        LastConnection.clear();
        backToTitle();
        game.ui.showErrorDialog("Kicked from lobby");
    }

    @Override
    public void closed(GameLobby lobby) {
        LastConnection.clear();
        backToTitle();
        game.ui.showErrorDialog("Lobby closed");
    }

    @Override
    public void disconnected(GameLobby lobby) {
        //disconnected is called on shutdown as well
        if (!game.shutdown) {
            backToTitle();
            game.ui.showErrorDialog("Connection problems");
        }
    }

    private void updateLobby() {
        AbstractScreen screen = game.ui.getCurrentScreen();
        if (screen instanceof LobbyScreen) {
            ((LobbyScreen) screen).updateLobby();
        }
    }

    private void backToTitle() {
        game.ui.showScreen(new StartScreen());
    }

}
