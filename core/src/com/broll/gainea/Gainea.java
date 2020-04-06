package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.client.InputHandler;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.client.render.MapRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;

public class Gainea extends ApplicationAdapter {

	public final static float EXPANSION_SIZE = 100;

	private SpriteBatch batch;
	private ShapeRenderer shape;

	private OrthographicCamera camera;
	private StretchViewport viewport;

	private MapRender mapRender = new MapRender();

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.position.setZero();
		viewport = new StretchViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),camera);
		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		initExpansion(new GaineaMap());
		initExpansion(new IcelandMap());
		initExpansion(new BoglandMap());
		initExpansion(new MountainsMap());
		Gdx.input.setInputProcessor(new InputHandler(camera));
	}

	private void initExpansion(ExpansionFactory factory){
		Expansion expansion =factory.create();
		ExpansionRender expansionRender = factory.createRender();
		expansionRender.init(expansion);
		mapRender.addRender(expansionRender);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.35f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		shape.setProjectionMatrix(camera.combined);
		batch.begin();
		mapRender.render(batch);
		batch.end();
		mapRender.render(shape);
	}

	@Override
	public void dispose () {
		batch.dispose();
		shape.dispose();
	}
}
