package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;

import java.util.Collection;

public class UnitRender extends MapObjectRender {

    private Texture plate;

    public UnitRender(Gainea game, NT_Unit unit) {
        super(game, unit);
        this.plate = game.assets.get("textures/unit_plate.png", Texture.class);
        setZIndex(0);
    }

    @Override
    protected void init() {
        NT_Unit unit = (NT_Unit) getObject();
        int color = 0;
        if (unit.owner != NT_Unit.NO_OWNER) {
            color = unit.owner + 1;
        }
        setChipColor(color);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (stackTop) {
            if (((OrthographicCamera) getStage().getCamera()).zoom < 2f) {
                batch.draw(plate, getX() - 37-R, getY() + 20-R);
            }
        }
        super.draw(batch, parentAlpha);
    }
}
