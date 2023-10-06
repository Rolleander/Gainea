package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.unit.MonsterRender;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.Collection;

public class MapObjectRender extends Actor {

    protected final static int R = 50;
    protected Gainea game;
    protected Collection<MapObjectRender> stack;
    protected boolean stackTop = true;
    protected Color renderColor = new Color();
    private NT_BoardObject object;
    private TextureRegion chip;
    private Location location;
    private TextureRegion icon;
    private float stackHeight;

    public MapObjectRender(Gainea game, Skin skin, NT_BoardObject object) {
        this.game = game;
        this.object = object;
        setSize(R * 2, R * 2);
        init();
        icon = TextureUtils.unitIcon(game, object.icon);
    }

    public static MapObjectRender createRender(Gainea gainea, Skin skin, NT_BoardObject object) {
        if (object instanceof NT_Monster) {
            return new MonsterRender(gainea, skin, (NT_Monster) object);
        }
        if (object instanceof NT_Unit) {
            return new UnitRender(gainea, skin, (NT_Unit) object);
        }
        return new MapObjectRender(gainea, skin, object);
    }

    public int getRank() {
        if (object instanceof NT_Unit) {
            NT_Unit unit = (NT_Unit) object;
            return unit.power + unit.maxHealth;
        }
        return 0;
    }

    public void selectionListener() {
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                game.ui.inGameUI.selectStack(location, stack);
                return true;
            }
        });
    }

    //Fix for table
    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        setX(getX() + width / 2);
        setY(getY() + height / 2);
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

    public void setStack(Collection<MapObjectRender> stack, float stackHeight) {
        this.stack = stack;
        this.stackHeight = stackHeight;
    }

    public void setStackTop(boolean stackTop) {
        this.stackTop = stackTop;
    }

    protected void init() {
        setChipColor(0);
    }

    protected void setChipColor(int color) {
        Texture texture = game.assets.get("textures/chips.png", Texture.class);
        chip = new TextureRegion(texture, color * R * 2, 0, R * 2, R * 2);
    }

    public void flip() {
        icon.flip(true, false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        calcRenderColor(parentAlpha);
        batch.setColor(renderColor);
        batch.draw(chip, getX() - R, getY() - R);
        batch.draw(icon, getX() - R + 9, getY() - R + 9);
    }

    protected void calcRenderColor(float parentAlpha) {
        renderColor.set(this.getColor()).mul(1, 1, 1, parentAlpha);
    }

    public NT_BoardObject getObject() {
        return object;
    }

    public float getStackHeight() {
        return stackHeight;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
