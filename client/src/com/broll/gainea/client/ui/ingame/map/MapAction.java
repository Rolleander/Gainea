package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.net.NT_Unit;

import java.util.List;
import java.util.Objects;

public class MapAction extends DepthActor {

    public final static int DEPTH = 250;
    public final static int TYPE_MOVE = 0;
    public final static int TYPE_ATTACK = 1;
    public final static int TYPE_PLACE = 2;
    private final static int SIZE = 100;
    private float animationAngle;
    private ActionTrail trail;
    private int locationId;
    private int from, to;
    private int type;
    private Object action;
    private List<NT_Unit> units;

    private boolean hover = true;

    private TextureRegion region;

    public MapAction(Gainea game, int type, int locationId, ActionListener clicked) {
        depth = 500;
        setVisible(false);
        this.type = type;
        this.locationId = locationId;
        Texture texture = game.assets.get("textures/map_actions.png", Texture.class);
        region = new TextureRegion(texture, SIZE * type, 0, SIZE, SIZE);
        setSize(SIZE, SIZE);
        setOrigin(Align.center);
        if (clicked != null) {
            hover = false;
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clicked.action();
                    if (trail != null) {
                        trail.remove();
                    }
                    AudioPlayer.playSound("select.ogg");
                    remove();
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    hover = true;
                    if (trail != null) {
                        trail.hover = true;
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    hover = false;
                    if (trail != null) {
                        trail.hover = false;
                    }
                }
            });
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float alpha = 0.65f;
        if (hover) {
            alpha = 1f;
        }
        setAlphaColor(batch, alpha * parentAlpha);
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        resetAlphaColor(batch);
    }

    public List<NT_Unit> getUnits() {
        return units;
    }

    public void setUnits(List<NT_Unit> units) {
        this.units = units;
    }

    @Override
    public void toFront() {
        if (trail != null) {
            trail.toFront();
        }
        super.toFront();
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x - SIZE / 2, y - SIZE / 2);
    }

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public void setFromTo(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, type);
    }

    public void setTrail(ActionTrail trail) {
        this.trail = trail;
    }

    @Override
    public boolean remove() {
        if (trail != null) {
            trail.remove();
        }
        return super.remove();
    }

    public int getLocationId() {
        return locationId;
    }

    public int getType() {
        return type;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (trail != null) trail.setVisible(visible);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        animationAngle += 0.1;
        setY((float) (getY() + Math.sin(animationAngle) * 0.4f));
    }

}
