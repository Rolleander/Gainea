package com.broll.gainea.client.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.network.IClientListener;
import com.broll.gainea.client.ui.ingame.map.MapScrollHandler;
import com.broll.gainea.client.ui.components.ConnectionCircle;
import com.broll.gainea.client.ui.components.NetworkProblemDialog;
import com.broll.gainea.client.ui.ingame.InGameUI;
import com.broll.gainea.client.ui.screens.LobbyScreen;
import com.broll.gainea.client.ui.screens.StartScreen;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.tasks.DiscoveredLobbies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GameUI implements IClientListener {

    private final static Logger Log = LoggerFactory.getLogger(GameUI.class);
    public Skin skin;
    public InGameUI inGameUI;
    public MapScrollHandler mapScrollHandler;
    private Gainea game;
    private ConnectionCircle connectionCircle;
    private AbstractScreen currentScreen;
    private boolean reconnectCheck = false;

    public GameUI(Gainea game, AbstractScreen startScreen, boolean reconnectCheck) {
        this.game = game;
        this.reconnectCheck = reconnectCheck;
        this.mapScrollHandler = new  MapScrollHandler(game, game.gameStage);
        game.assets = new Assets();
        showScreen(startScreen);
    }

    public void assetsLoaded() {
        this.skin = game.assets.get("ui/cloud-form-ui.json", Skin.class);
        connectionCircle = new ConnectionCircle(game.assets);
        connectionCircle.toFront();
        if (reconnectCheck) {
            game.client.reconnectCheck();
        }
    }

    public void initInGameUi() {
        game.gameStage.clear();
        inGameUI = new InGameUI(game, skin);
        game.gameStage.addListener(mapScrollHandler);
    }

    public void showScreen(AbstractScreen screen) {
        this.currentScreen = screen;
        game.gameStage.clear();
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

    public AbstractScreen getCurrentScreen() {
        return currentScreen;
    }
}
