package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;

import java.util.Collection;

public class UnitRender extends MapObjectRender {

    private Texture plate;
    private boolean hidePlate;
    private boolean alwaysDrawPlate;
    private Label numberLabel;

    public UnitRender(Gainea game, Skin skin, NT_Unit unit) {
        super(game, skin, unit);
        this.plate = game.assets.get("textures/unit_plate.png", Texture.class);
        setWidth(R * 2 + 37 * 2);
        setZIndex(0);
        numberLabel = LabelUtils.label(skin, "");
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

    public void setAlwaysDrawPlate(boolean alwaysDrawPlate) {
        this.alwaysDrawPlate = alwaysDrawPlate;
    }

    protected boolean shouldDrawPlate() {
        if (alwaysDrawPlate) {
            return true;
        }
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
            NT_Unit unit = (NT_Unit) getObject();
            batch.draw(plate, getX() - 37 - R, getY() + 20 - R);
            numberLabel.setStyle(new Label.LabelStyle(numberLabel.getStyle().font, Color.BLACK));
            numberLabel.setPosition(getX() - 70, getY() - 17);
            numberLabel.setText("" + unit.power);
            numberLabel.draw(batch, parentAlpha);
            numberLabel.setPosition(getX() + 64, getY() - 17);
            numberLabel.setText("" + unit.health);
            if (unit.health < unit.maxHealth) {
                numberLabel.setStyle(new Label.LabelStyle(numberLabel.getStyle().font, Color.RED));
            }
            numberLabel.draw(batch, parentAlpha);

        }
        super.draw(batch, parentAlpha);
    }

}
