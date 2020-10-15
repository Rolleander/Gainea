package com.broll.gainea.client.ui.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_Monster;

public class MonsterRender extends UnitRender {
    private TextureRegion starPlate;
    private final static int W = 154;
    private final static int H = 74;


    public MonsterRender(Gainea game, Skin skin, NT_Monster unit) {
        super(game, skin, unit);
        int stars = unit.stars;
        setHeight(R * 2 + 48);
        starPlate = new TextureRegion(game.assets.get("textures/star_plates.png", Texture.class), stars * W, 0, W, H);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //draw star plate
        if (shouldDrawPlate()) {
            batch.draw(starPlate, getX() - R - 26, getY() - R + 48);
        }
        super.draw(batch, parentAlpha);
    }
}
