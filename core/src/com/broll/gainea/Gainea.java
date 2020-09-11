package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.ClientHandler;
import com.broll.gainea.client.MapScrollHandler;
import com.broll.gainea.client.ui.GameUI;

public class Gainea extends ApplicationAdapter {

    public final static float EXPANSION_SIZE = 100;

    public ClientHandler client;
    public Stage gameStage;
    public Stage uiStage;
    public GameUI ui;
    public Assets assets;
    public ShapeRenderer shapeRenderer;
    public boolean shutdown=false;

    @Override
    public void create() {
        //new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
        client = new ClientHandler();
        gameStage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ScreenViewport());
        ui = new GameUI(this);
        client.setClientListener(ui);
        gameStage.addListener(new MapScrollHandler((OrthographicCamera) gameStage.getCamera()));
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, gameStage));
        shapeRenderer= new ShapeRenderer();
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
        gameStage.act(delta);
        uiStage.act(delta);
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
        shapeRenderer.dispose();
    }
}
