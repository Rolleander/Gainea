package com.broll.gainea.client.network;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.client.ui.screens.StartScreen;
import com.broll.mpnll.client.lobby.Lobby;
import com.broll.mpnll.client.lobby.LobbyListener;
import com.broll.mpnll.client.lobby.User;

public class ClientLobbyListener implements LobbyListener {
    private final Gainea game;
    public ClientLobbyListener(Gainea game) { this.game = game; }
    @Override public void lobbyUpdated(Lobby lobby) { onGdx(this::updateLobby); }
    @Override public void userJoined(Lobby lobby, User user) { onGdx(this::updateLobby); }
    @Override public void userLeft(Lobby lobby, User user) { onGdx(this::updateLobby); }
    @Override public void closed(Lobby lobby) { onGdx(() -> { backToTitle(); game.ui.showErrorDialog("Lobby closed"); }); }
    private void onGdx(Runnable action) { Gdx.app.postRunnable(action); }
    private void updateLobby() { Screen screen=game.ui.getCurrentScreen(); if (screen instanceof LobbyScreen) ((LobbyScreen)screen).updateLobby(); }
    private void backToTitle() { if (!game.shutdown) game.ui.showScreen(new StartScreen()); }
}
