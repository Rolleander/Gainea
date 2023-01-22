package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.network.ClientHandler;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.GameUI;
import com.broll.gainea.client.ui.screens.LoadingScreen;
import com.broll.gainea.client.ui.screens.StartScreen;

public class Gainea extends ApplicationAdapter {

    public ClientHandler client;
    public Stage gameStage;
    public Stage uiStage;
    public GameUI ui;
    public Assets assets;
    public ShapeRenderer uiShapeRenderer;
    public ShapeRenderer gameShapeRenderer;
    public GameState state;
    public boolean shutdown = false;
    private Screen startScreen;
    private boolean reconnectCheck;

    public Gainea() {
        this(new StartScreen(), true);
    }

    public Gainea(Screen startScreen, boolean reconnectCheck) {
        this.startScreen = startScreen;
        this.reconnectCheck = reconnectCheck;
    }

    @Override
    public void create() {
        //new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
        client = new ClientHandler(this);
        gameStage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ScreenViewport());
        state = new GameState(this);
        ui = new GameUI(this, new LoadingScreen(startScreen), reconnectCheck);
        client.setClientListener(ui);
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, gameStage));
        uiShapeRenderer = new ShapeRenderer();
        gameShapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.3f, 0.35f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiShapeRenderer.setProjectionMatrix(uiStage.getViewport().getCamera().combined);
        gameShapeRenderer.setProjectionMatrix(gameStage.getViewport().getCamera().combined);
        gameStage.act(delta);
        uiStage.act(delta);
        ui.mapScrollHandler.update(delta);
        gameStage.draw();
        uiStage.draw();
    }

    @Override
    public void dispose() {
        shutdown = true;
        assets.getManager().dispose();
        gameStage.dispose();
        uiStage.dispose();
        client.shutdown();
        uiShapeRenderer.dispose();
        gameShapeRenderer.dispose();
    }
}
