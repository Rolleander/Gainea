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
    private boolean hidePlate;

    public UnitRender(Gainea game, NT_Unit unit) {
        super(game, unit);
        this.plate = game.assets.get("textures/unit_plate.png", Texture.class);
        setWidth(R*2+37*2);
        setZIndex(0);
    }

    public void setHidePlate(boolean hidePlate) {
        this.hidePlate = hidePlate;
    }

    @Override
    protected void init() {
        NT_Unit unit = (NT_Unit) getObject();
        int color = 0;
        if (unit.owner != NT_Unit.NO_OWNER) {
            color = game.state.getPlayer(unit.owner).color + 1;
        }
        setChipColor(color);
    }

    protected boolean shouldDrawPlate() {
        if (stackTop) {
            if (!hidePlate && ((OrthographicCamera) getStage().getCamera()).zoom < 2f) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (shouldDrawPlate()) {
            batch.draw(plate, getX() - 37 - R, getY() + 20 - R);
        }
        super.draw(batch, parentAlpha);
    }
}
