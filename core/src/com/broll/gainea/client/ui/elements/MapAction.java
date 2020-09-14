package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.broll.gainea.Gainea;

import java.util.Objects;

public class MapAction extends Image {

    private final static int SIZE = 100;
    private float animationAngle;
    private ActionTrail trail;
    private int unitId;
    private int from, to;
    private int type;

    public MapAction(Gainea game, int type, int unitId, ActionListener clicked) {
        setVisible(false);
        this.type = type;
        this.unitId = unitId;
        Texture texture = game.assets.get("textures/map_actions.png", Texture.class);
        TextureRegion region = new TextureRegion(texture, SIZE * type, 0, SIZE, SIZE);
        setDrawable(new TextureRegionDrawable(region));
        setSize(SIZE, SIZE);
        setOrigin(Align.center);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clicked.action();
                if (trail != null) {
                    trail.remove();
                }
                remove();
            }
        });
    }

    public void setFromTo(int from, int to) {
        this.from = from;
        this.to = to;
    }

    /**
     * same map action if type. from and to are same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapAction action = (MapAction) o;
        return from == action.from &&
                to == action.to &&
                type == action.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, type);
    }

    public void setTrail(ActionTrail trail) {
        this.trail = trail;
    }

    public int getUnitId() {
        return unitId;
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
