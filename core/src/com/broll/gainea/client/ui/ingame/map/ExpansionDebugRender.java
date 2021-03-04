package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExpansionDebugRender extends Actor {

    private Expansion expansion;
    private Gainea game;

    public ExpansionDebugRender(Gainea game, Expansion expansion) {
        this.game = game;
        this.expansion = expansion;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        render(game.gameShapeRenderer);
        batch.begin();
    }

    public void render(ShapeRenderer shape) {
        expansion.getContents().forEach(content -> {
            content.getAreas().forEach(area -> renderArea(area, shape));
            content.getShips().forEach(ship -> renderShip(ship, shape));
        });
    }

    private void renderArea(Area location, ShapeRenderer shape) {
        float x = this.getX() + location.getCoordinates().getDisplayX();
        float y = this.getY() + location.getCoordinates().getDisplayY();
        float r = 25;
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.RED);
        shape.circle(x, y, r);
        shape.end();
        renderConnections(location, location.getConnectedLocations(), shape);
    }

    private void renderShip(Ship location, ShapeRenderer shape) {
        float x = this.getX() + location.getCoordinates().getDisplayX();
        float y = this.getY() + location.getCoordinates().getDisplayY();
        float r = 25;
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BROWN);
        shape.circle(x, y, r);
        shape.end();
        List<Location> cons = new ArrayList<>();
        cons.add(location.getFrom());
        cons.add(location.getTo());
        renderConnections(location, cons, shape);
    }

    private void renderConnections(Location location, Collection<Location> connections, ShapeRenderer shape) {
        float x = this.getX() + location.getCoordinates().getDisplayX();
        float y = this.getY() + location.getCoordinates().getDisplayY();
        connections.forEach(loc -> {
            float tx = this.getX() + loc.getCoordinates().getDisplayX();
            float ty = this.getY() + loc.getCoordinates().getDisplayY();
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.BLACK);
            shape.line(x, y, tx, ty);
            shape.end();
        });
    }
}
