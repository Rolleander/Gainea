package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.IClientListener;
import com.broll.gainea.client.ui.screens.LoadingScreen;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.client.ui.screens.StartScreen;
import com.broll.networklib.client.auth.LastConnection;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.DiscoveredLobbies;
import com.esotericsoftware.minlog.Log;

public class GameUI implements IClientListener {

    private Gainea game;
    private Skin skin;
    private ConnectionCircle connectionCircle;
    private AbstractScreen currentScreen;
    private InGameUI inGameUI;

    public GameUI(Gainea game) {
        this.game = game;
        game.assets = new Assets();
        showScreen(new LoadingScreen());
    }

    public void assetsLoaded() {
        this.skin = game.assets.get("ui/cloud-form-ui.json", Skin.class);
        inGameUI = new InGameUI(game, skin);
        connectionCircle = new ConnectionCircle(game.assets);
        connectionCircle.toFront();
        game.client.reconnectCheck();
    }

    public void showScreen(AbstractScreen screen) {
        this.currentScreen = screen;
        game.uiStage.clear();
        screen.init(skin, game);
        game.uiStage.addActor(screen.build());
    }

    @Override
    public void discoveredLobbies(DiscoveredLobbies lobbies) {
        if (currentScreen instanceof StartScreen) {
            ((StartScreen) currentScreen).showLobbies(lobbies);
        }
    }

    @Override
    public void connectedLobby(GameLobby lobby) {
        Log.info("join lobby " + lobby.getName());
        showScreen(new LobbyScreen(lobby));
    }

    @Override
    public void connectionFailure(String reason) {
        Log.info("failure " + reason);
        showErrorDialog(reason);
    }

    public void showErrorDialog(String error) {
        game.uiStage.addActor(new NetworkProblemDialog(game, skin, error));
    }

    @Override
    public void loadingStateUpdate(boolean loading) {
        if (loading) {
            game.uiStage.addActor(connectionCircle);
        } else {
            connectionCircle.remove();
        }
    }

    public InGameUI getInGameUI() {
        return inGameUI;
    }

    public Skin getSkin() {
        return skin;
    }
}
