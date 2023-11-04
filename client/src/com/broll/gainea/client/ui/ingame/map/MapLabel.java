package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class MapLabel extends DepthActor {

    private Label label;

    public MapLabel(Gainea game, String name) {
        depth = -50;
        label = LabelUtils.title(game.ui.skin, name);
        label.setFontScale(0.85f);
        label.pack();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        label.setPosition(x - label.getWidth() / 2, y - label.getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        label.draw(batch, 0.7f);
    }
}
