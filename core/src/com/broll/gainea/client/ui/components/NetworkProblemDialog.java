package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.broll.gainea.Gainea;

public class NetworkProblemDialog extends Table {

    private Gainea game;

    public NetworkProblemDialog(Gainea game, Skin skin, String message) {
        this.game = game;
        setFillParent(true);
        Table dialog = new Table(skin);
        dialog.pad(30, 20, 10, 20);
        dialog.setOrigin(Align.center);
        dialog.setBackground("window");
        dialog.center();
        Label l = new Label(message, skin);
        l.setStyle(new Label.LabelStyle(skin.getFont("title"), Color.RED));
        l.setFontScale(0.8f);
        dialog.add(l);
        add(dialog);
        toFront();
        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.removeActor()));
                return true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //make background darker
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.uiShapeRenderer.setColor(0, 0, 0, 0.5f);
        game.uiShapeRenderer.rect(0, 0, getWidth(), getHeight());
        game.uiShapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        super.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return this;
    }
}
