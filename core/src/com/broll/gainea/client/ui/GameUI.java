package com.broll.gainea.client.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.network.IClientListener;
import com.broll.gainea.client.ui.ingame.map.MapScrollControl;
import com.broll.gainea.client.ui.ingame.map.MapScrollGestureHandler;
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
    public MapScrollControl mapScrollControl;
    private Gainea game;
    private ConnectionCircle connectionCircle;
    private Screen currentScreen;
    private boolean reconnectCheck = false;

    public GameUI(Gainea game, Screen startScreen, boolean reconnectCheck) {
        this.game = game;
        this.reconnectCheck = reconnectCheck;
        game.assets = new Assets();
        showScreen(startScreen);
    }

    public void assetsLoaded() {
        this.skin = game.assets.get("ui/cloud-form-ui.json", Skin.class);
        skin.getFont("font").getData().markupEnabled = true;
        connectionCircle = new ConnectionCircle(game.assets);
        connectionCircle.toFront();
        if (reconnectCheck) {
            game.client.reconnectCheck();
        }
    }

    public void initInGameUi() {
        game.gameStage.clear();
        inGameUI = new InGameUI(game, skin);
        if(Gdx.app.getType() != Application.ApplicationType.Desktop){
            Log.info("Setup MapScrollGestureHandler");
            MapScrollGestureHandler handler = new MapScrollGestureHandler(game);
            this.mapScrollControl = handler.getMapScrollControl();
            game.gameStage.addListener(handler);
        }
        else{
            Log.info("Setup MapScrollHandler");
            MapScrollHandler handler = new MapScrollHandler(game);
            this.mapScrollControl = handler.getMapScrollControl();
            game.gameStage.addListener(handler);
        }
    }

    public void showScreen(Screen screen) {
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

    public Screen getCurrentScreen() {
        return currentScreen;
    }
}
