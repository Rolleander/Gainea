package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.IconUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;
import com.esotericsoftware.minlog.Log;

import java.util.Collection;

public class MapObjectRender extends Actor {

    protected final static int R = 50;
    private Gainea game;
    private NT_BoardObject object;
    private TextureRegion chip;
    protected Collection<MapObjectRender> stack;
    protected boolean stackTop;
    private Location location;
    private TextureRegion icon;

    public MapObjectRender(Gainea game, NT_BoardObject object) {
        this.game = game;
        this.object = object;
        init();
        icon = IconUtils.unitIcon(game, object.icon);
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                game.ui.getInGameUI().selectStack(location, stack);
                return true;
            }
        });
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        if (!isVisible()) return null;
        if (Vector2.dst(0, 0, x, y) < R) {
            return this;
        }
        return null;
    }

    public void setStack(Collection<MapObjectRender> stack, boolean top) {
        this.stack = stack;
        this.stackTop = top;
    }

    protected void init() {
        setChipColor(0);
    }

    protected void setChipColor(int color) {
        Texture texture = game.assets.get("textures/chips.png", Texture.class);
        chip = new TextureRegion(texture, color * R * 2, 0, R * 2, R * 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(chip, getX() - R, getY() - R);
        batch.draw(icon, getX() - R + 9, getY() - R + 9);
    }

    public NT_BoardObject getObject() {
        return object;
    }

    public static MapObjectRender createRender(Gainea gainea, NT_BoardObject object) {
        if (object instanceof NT_Unit) {
            return new UnitRender(gainea, (NT_Unit) object);
        }
        return new MapObjectRender(gainea, object);
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
