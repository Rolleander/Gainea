package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;

public class UnitRender extends MapObjectRender {

    public static float GLOW_ANIMATION;
    private static int BLOOD_SIZE = 150;
    private static float ATTACK_MOVE = 50;
    protected Label numberLabel;
    protected Label.LabelStyle blackStyle, redStyle;
    private Texture plate;
    private Texture activeRing;
    private boolean actionActive = false;
    private boolean hidePlate;
    private boolean alwaysDrawPlate;
    private Animation<TextureRegion> blood;
    private float bloodAnimation;
    private boolean showBlood = false;

    public UnitRender(Gainea game, Skin skin, NT_Unit unit) {
        super(game, skin, unit);
        this.plate = game.assets.get("textures/unit_plate.png", Texture.class);
        this.activeRing = game.assets.get("textures/active_ring.png", Texture.class);
        setWidth(radius * 2 + 37 * 2);
        numberLabel = LabelUtils.label(skin, "");
        blackStyle = numberLabel.getStyle();
        redStyle = new Label.LabelStyle(blackStyle.font, Color.RED);
        blood = new Animation<>(0.05f, TextureUtils.split(game.assets.get("textures/blood.png", Texture.class), BLOOD_SIZE, BLOOD_SIZE));
    }

    public boolean isActionActive() {
        return actionActive;
    }

    public void setActionActive(boolean b) {
        actionActive = b;
    }

    public void setHidePlate(boolean hidePlate) {
        this.hidePlate = hidePlate;
    }


    public void setAlwaysDrawPlate(boolean alwaysDrawPlate) {
        this.alwaysDrawPlate = alwaysDrawPlate;
    }

    public NT_Unit getUnit() {
        return (NT_Unit) getObject();
    }

    public void takeDamage(int amount) {
        NT_Unit unit = getUnit();
        boolean wasAlive = unit.health > 0;
        unit.health -= amount;
        if (wasAlive) {
            AudioPlayer.playSound("damage.ogg");
            if (unit.health <= 0) {
                kill(false);
            }
        }
        bloodAnimation = 0;
        showBlood = true;
    }

    public void kill(boolean removeActor) {
        NT_Unit unit = getUnit();
        deathSound(unit);
        unit.health = 0;
        if (removeActor) {
            addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.removeActor()));
        }
        bloodAnimation = 0;
        showBlood = true;
    }

    public void attack(UnitRender target) {
        float x = target.getX() > this.getX() ? ATTACK_MOVE : -ATTACK_MOVE;
        float y = (target.getY() - this.getY()) / 2;
        addAction(Actions.sequence(Actions.moveBy(x, y, 0.1f, Interpolation.exp10Out),
                Actions.moveBy(-x, -y, 0.2f, Interpolation.sineIn)));
    }

    private void deathSound(NT_Unit unit) {
        AudioPlayer.playSound("kill.ogg");
        if (unit instanceof NT_Monster) {
            AudioPlayer.playSound("monster_death.ogg");
        } else {
            int type = unit.type;
            if (type == NT_Unit.TYPE_MALE) {
                AudioPlayer.playSound("death_male.ogg");
            } else if (type == NT_Unit.TYPE_FEMALE) {
                AudioPlayer.playSound("death_female.ogg");
            }
        }
    }

    protected boolean shouldDrawPlate() {
        if (alwaysDrawPlate) {
            return true;
        }
        if (stackTop) {
            return !hidePlate && ((OrthographicCamera) getStage().getCamera()).zoom < 2f;
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setRenderColor(batch, parentAlpha);
        if (shouldDrawPlate()) {
            NT_Unit unit = (NT_Unit) getObject();
            batch.draw(plate, getX() - 37 - radius, getY() + 20 - radius);
            numberLabel.setColor(batch.getColor());
            numberLabel.setStyle(blackStyle);
            numberLabel.setPosition(getX() - 74, getY() - 16);
            numberLabel.setText("" + unit.power);
            numberLabel.draw(batch, parentAlpha);
            numberLabel.setPosition(getX() + 58, getY() - 16);
            numberLabel.setText("" + unit.health);
            if (unit.health < unit.maxHealth) {
                numberLabel.setStyle(redStyle);
            }
            numberLabel.draw(batch, parentAlpha);
        }
        resetRenderColor(batch);
        super.draw(batch, parentAlpha);
        setRenderColor(batch, parentAlpha);
        if (actionActive) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, MathUtils.clamp((float) (Math.sin(GLOW_ANIMATION) * 0.5f + 0.7f), 0.2f, 1));
            batch.draw(activeRing, getX() - activeRing.getWidth() / 2f, getY() - activeRing.getHeight() / 2f);
        }
        resetRenderColor(batch);
        if (showBlood) {
            bloodAnimation += Gdx.graphics.getDeltaTime();
            batch.draw(blood.getKeyFrame(bloodAnimation), getX() - BLOOD_SIZE / 2f, getY() - BLOOD_SIZE / 2f);
            if (blood.isAnimationFinished(bloodAnimation)) {
                showBlood = false;
            }
        }
    }

    public boolean removeIfDead() {
        boolean dead = getUnit().health <= 0;
        if (dead) {
            addAction(Actions.sequence(Actions.fadeOut(0.2f), Actions.visible(false)));
        }
        return dead;
    }

}
