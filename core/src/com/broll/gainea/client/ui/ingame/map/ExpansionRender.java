package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExpansionRender extends Actor {

    public final static float SIZE = 3120;
    private String textureName;
    private Texture texture;
    private Expansion expansion;
    private Gainea game;

    public ExpansionRender(String textureName) {
        this.textureName = textureName;
    }

    public void init(Gainea game, Expansion expansion) {
        this.game = game;
        this.expansion = expansion;
        texture = game.assets.get("textures/" + textureName, Texture.class);
        Coordinates coords = expansion.getCoordinates();
        coords.calcDisplayLocation(SIZE);
        expansion.getAllLocations().stream().map(Location::getCoordinates).forEach(it -> {
            it.shift(-0.5f, 0);
            it.mirrorY(0.5f);
            it.calcDisplayLocation(SIZE);
        });
        this.setBounds(coords.getDisplayX() - SIZE / 2, coords.getDisplayY() - SIZE / 2, SIZE, SIZE);
    }

    public Expansion getExpansion() {
        return expansion;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY());
    }

}
