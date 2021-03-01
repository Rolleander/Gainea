package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.net.NT_Monster;

public class MonsterRender extends UnitRender {
    private TextureRegion starPlate;
    private final static int W = 154;
    private final static int H = 74;


    public MonsterRender(Gainea game, Skin skin, NT_Monster unit) {
        super(game, skin, unit);
        int stars = unit.stars;
        setHeight(MapObjectRender.R * 2 + 48);
        starPlate = new TextureRegion(game.assets.get("textures/star_plates.png", Texture.class), stars * W, 0, W, H);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        calcRenderColor(parentAlpha);
        batch.setColor(renderColor);
        //draw star plate
        if (shouldDrawPlate()) {
            batch.draw(starPlate, getX() - MapObjectRender.R - 26, getY() - MapObjectRender.R + 48);
        }
        super.draw(batch, parentAlpha);
    }
}
