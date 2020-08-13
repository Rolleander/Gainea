package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.broll.gainea.client.MapScrollHandler;
import com.broll.gainea.client.ui.GameUI;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;
import com.broll.networklib.client.LobbyGameClient;

public class Gainea extends ApplicationAdapter {

    public final static float EXPANSION_SIZE = 100;

    private Stage gameStage;
    private Stage uiStage;
    private GameUI gameUI;
    private LobbyGameClient client;

    @Override
    public void create() {
        TextField.keyRepeatInitialTime=0.1f;
        TextField.keyRepeatTime=0.005f;

        //new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())
        client =new LobbyGameClient(NetworkSetup::registerNetwork);
        NetworkSetup.setup(client);
        gameStage = new Stage(new ScreenViewport());
        uiStage = new Stage(new ScreenViewport());
        gameUI =new GameUI(uiStage, client);
        gameStage.addListener(new MapScrollHandler((OrthographicCamera) gameStage.getCamera()));
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, gameStage));
        initExpansion(new GaineaMap());
        initExpansion(new IcelandMap());
          initExpansion(new BoglandMap());
          initExpansion(new MountainsMap());
    }

    private void initExpansion(ExpansionFactory factory) {
        Expansion expansion = factory.create();
        ExpansionRender expansionRender = factory.createRender();
        expansionRender.init(expansion);
        gameStage.addActor(expansionRender);
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height);
        uiStage.getViewport().update(width,height,true);
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
        gameStage.dispose();
        uiStage.dispose();
    }
}
