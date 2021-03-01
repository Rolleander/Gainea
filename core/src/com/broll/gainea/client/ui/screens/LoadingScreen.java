package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.AbstractScreen;

public class LoadingScreen extends AbstractScreen {

    private LoadingActor loadingActor = new LoadingActor();

    private AbstractScreen startScreen;

    public LoadingScreen(AbstractScreen startScreen) {
        this.startScreen = startScreen;
    }

    private class LoadingActor extends Actor {
        @Override
        public void draw(Batch batch, float parentAlpha) {

            float progess = game.assets.getManager().getProgress();
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            float b = 50;
            float h = 50;

            float y = getStage().getHeight() / 2;
            float w = getStage().getWidth() - b * 2;

            float cw = MathUtils.lerp(0, w, progess);

            game.shapeRenderer.setColor(Color.WHITE);
            game.shapeRenderer.rect(b, y - h / 2, cw, h);

            game.shapeRenderer.end();
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (game.assets.getManager().update()) {
                loadingDone();
            }
        }
    }

    private void loadingDone() {
        AudioPlayer.init(game.assets);
        game.ui.assetsLoaded();
        game.ui.showScreen(startScreen);
    }

    @Override
    public Actor build() {
        return loadingActor;
    }
}
