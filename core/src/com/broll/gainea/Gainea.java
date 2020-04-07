package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.broll.gainea.client.MapScrollHandler;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;

public class Gainea extends ApplicationAdapter {

    public final static float EXPANSION_SIZE = 100;

    private Stage gameStage;
    private Stage uiStage;

    @Override
    public void create() {
        gameStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        uiStage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        gameStage.addListener(new MapScrollHandler((OrthographicCamera) gameStage.getCamera()));
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, gameStage));
        initExpansion(new GaineaMap());
        initExpansion(new IcelandMap());
        initExpansion(new BoglandMap());
        initExpansion(new MountainsMap());
		Table table =new Table();
		table.setFillParent(true);
		Skin skin = new Skin();
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		skin.add("default", new BitmapFont());
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = skin.getFont("default");
		skin.add("default", textButtonStyle);
		table.add(new TextButton("click me",skin));
        uiStage.addActor(table);
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
        uiStage.getViewport().update(width,height);
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
