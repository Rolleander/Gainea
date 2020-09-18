package com.broll.gainea.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.ActionListener;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.TextureUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.esotericsoftware.minlog.Log;

import java.util.Collection;

public class UnitRender extends MapObjectRender {

    private Texture plate;
    private boolean hidePlate;
    private boolean alwaysDrawPlate;
    private Label numberLabel;
    private Label.LabelStyle blackStyle, redStyle;
    private Animation<TextureRegion> blood;
    private float bloodAnimation;
    private static int BLOOD_SIZE = 150;
    private boolean showBlood = false;
    private ActionListener deathListener;

    public UnitRender(Gainea game, Skin skin, NT_Unit unit) {
        super(game, skin, unit);
        this.plate = game.assets.get("textures/unit_plate.png", Texture.class);
        setWidth(R * 2 + 37 * 2);
        setZIndex(0);
        numberLabel = LabelUtils.label(skin, "");
        blackStyle = numberLabel.getStyle();
        redStyle = new Label.LabelStyle(blackStyle.font, Color.RED);
        blood = new Animation<>(0.05f, TextureUtils.split(game.assets.get("textures/blood.png", Texture.class), BLOOD_SIZE, BLOOD_SIZE));
    }

    public void setHidePlate(boolean hidePlate) {
        this.hidePlate = hidePlate;
    }

    public void setDeathListener(ActionListener deathListener) {
        this.deathListener = deathListener;
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

    public NT_Unit getUnit() {
        return (NT_Unit) getObject();
    }

    public void takeDamage(int amount) {
        NT_Unit unit = getUnit();
        unit.health -= amount;
        game.assets.get("sounds/damage.ogg", Sound.class).play();
        if (unit.health < 0) {
            unit.health = 0;
        }
        bloodAnimation = 0;
        showBlood = true;
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
        if (!isVisible()) {
            return;
        }
        if (shouldDrawPlate()) {
            NT_Unit unit = (NT_Unit) getObject();
            batch.draw(plate, getX() - 37 - R, getY() + 20 - R);
            numberLabel.setStyle(blackStyle);
            numberLabel.setPosition(getX() - 70, getY() - 17);
            numberLabel.setText("" + unit.power);
            numberLabel.draw(batch, parentAlpha);
            numberLabel.setPosition(getX() + 64, getY() - 17);
            numberLabel.setText("" + unit.health);
            if (unit.health < unit.maxHealth) {
                numberLabel.setStyle(redStyle);
            }
            numberLabel.draw(batch, parentAlpha);

        }
        super.draw(batch, parentAlpha);
        if (showBlood) {
            bloodAnimation += Gdx.graphics.getDeltaTime();
            batch.draw(blood.getKeyFrame(bloodAnimation), getX() - BLOOD_SIZE / 2, getY() - BLOOD_SIZE / 2);
            if (blood.isAnimationFinished(bloodAnimation)) {
                showBlood = false;
                if (getUnit().health <= 0) {
                    remove();
                    if (deathListener != null) {
                        deathListener.action();
                    }
                }
            }
        }
    }

}
