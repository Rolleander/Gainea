package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.client.ui.ingame.unit.MonsterRender;
import com.broll.gainea.client.ui.ingame.unit.UnitRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

public class MapObjectRender extends DepthActor {

    protected final static int R = 50;
    private final static int DESCRIPTION_WIDTH = 220;
    public boolean spawned = true;
    public boolean showDescription = true;
    protected float radius;
    protected Gainea game;
    protected Collection<MapObjectRender> stack;
    protected boolean stackTop = true;
    protected float labelDisplacement = 5;
    private Color previousBatchColor;

    private Color renderColor;
    private NT_BoardObject object;
    private TextureRegion chip;
    private Location location;
    private TextureRegion icon;
    private Label infoLabel;
    private float stackHeight;

    private float scale;
    private int drawCall;

    public MapObjectRender(Gainea game, Skin skin, NT_BoardObject object) {
        depth = 50;
        this.game = game;
        this.scale = 1f;
        this.radius = R * scale;
        setSize(radius * 2, radius * 2);
        if (StringUtils.isNotEmpty(object.description)) {
            infoLabel = LabelUtils.markup(skin, "[DARK_GRAY]" + object.description);
            infoLabel.setWidth(DESCRIPTION_WIDTH);
            infoLabel.setAlignment(Align.center);
            LabelUtils.autoWrap(infoLabel, DESCRIPTION_WIDTH);
            infoLabel.setWidth(DESCRIPTION_WIDTH);
        }
        setChipColor(0);
        init(object);
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

    public void respectScale() {
        this.scale = object.scale;
        this.radius = R * scale;
        setSize(radius * 2, radius * 2);
    }

    public void init(NT_BoardObject object) {
        this.object = object;
        icon = TextureUtils.unitIcon(game, object.icon);
        if (StringUtils.isNotEmpty(object.description)) {
            infoLabel.setText("[DARK_GRAY]" + object.description);
        }
        int color = 0;
        if (object.owner != NT_BoardObject.NO_OWNER) {
            color = game.state.getPlayer(object.owner).color + 1;
        }
        setChipColor(color);
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
        if (Vector2.dst(0, 0, x, y) < radius) {
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


    protected void setChipColor(int color) {
        Texture texture = game.assets.get("textures/chips.png", Texture.class);
        chip = new TextureRegion(texture, color * R * 2, 0, R * 2, R * 2);
    }

    public void flip() {
        icon.flip(true, false);
    }

    private boolean shouldDrawInfo() {
        return showDescription && ((OrthographicCamera) getStage().getCamera()).zoom <= 1.5f && infoLabel != null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setRenderColor(batch, parentAlpha);
        batch.draw(chip, getX() - radius, getY() - radius, chip.getRegionWidth() * scale, chip.getRegionHeight() * scale);
        batch.draw(icon, getX() - radius + 9 * scale, getY() - radius + 9 * scale, icon.getRegionWidth() * scale, icon.getRegionHeight() * scale);
        if (shouldDrawInfo()) {
            infoLabel.setPosition(getX() - infoLabel.getWidth() / 2, getY() + radius + labelDisplacement);
            infoLabel.draw(batch, parentAlpha);
        }
        resetRenderColor(batch);
    }

    protected void setRenderColor(Batch batch, float parentAlpha) {
        if (drawCall == 0) {
            previousBatchColor = batch.getColor().cpy();
            renderColor = calcRenderColor(parentAlpha);
        }
        batch.setColor(renderColor);
        drawCall++;
    }

    protected void resetRenderColor(Batch batch) {
        drawCall--;
        batch.setColor(previousBatchColor);
    }

    private Color calcRenderColor(float parentAlpha) {
        Color c = getColor();
        return new Color(c.r, c.g, c.b, c.a * parentAlpha);
    }

    public NT_BoardObject getObject() {
        return object;
    }

    public float getStackHeight() {
        return stackHeight;
    }

    public Collection<MapObjectRender> getStack() {
        return stack;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
