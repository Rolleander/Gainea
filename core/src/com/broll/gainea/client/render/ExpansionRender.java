package com.broll.gainea.client.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.map.Area;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExpansionRender {

    private final static float SIZE = 3120;
    private float x;
    private float y;
    private String textureName;
    private Texture texture;
    private Expansion expansion;

    public ExpansionRender(String textureName) {
        this.textureName = textureName;
    }

    public void init(Expansion expansion) {
        this.expansion = expansion;
        texture = new Texture("textures/" + textureName);
        x = expansion.getCoordinates().getX(SIZE);
        y = expansion.getCoordinates().getY(SIZE);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x -SIZE/2, y-SIZE/2);
    }

    public void render(ShapeRenderer shape) {
        expansion.getContents().forEach(content -> {
            content.getAreas().forEach(area -> renderArea(area, shape));
           content.getShips().forEach(ship -> renderShip(ship, shape));
        });
    }

    private void renderArea(Area location, ShapeRenderer shape) {
        float x = this.x-SIZE/2+location.getCoordinates().getX(SIZE);
        float y = this.y+SIZE/2-location.getCoordinates().getY(SIZE);
        float r = 25;
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.RED);
        shape.circle(x,y,r);
        shape.end();
        renderConnections(location, location.getAdjacentLocations(),shape);
    }

    private void renderShip(Ship location, ShapeRenderer shape) {
        float x = this.x-SIZE/2+location.getCoordinates().getX(SIZE);
        float y = this.y+SIZE/2-location.getCoordinates().getY(SIZE);
        float r = 25;
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BROWN);
        shape.circle(x,y,r);
        shape.end();
        List<Location> cons = new ArrayList<>();
        cons.add(location.getFrom());
        cons.add(location.getTo());
        renderConnections(location, cons,shape);
    }

    private void renderConnections(Location location, Collection<Location> connections, ShapeRenderer shape){
        float x = this.x-SIZE/2+location.getCoordinates().getX(SIZE);
        float y = this.y+SIZE/2-location.getCoordinates().getY(SIZE);
        connections.forEach(loc->{
            float tx = this.x-SIZE/2+loc.getCoordinates().getX(SIZE);
            float ty = this.y+SIZE/2-loc.getCoordinates().getY(SIZE);
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.BLACK);
            shape.line(x,y,tx,ty);
            shape.end();
        });
    }

}
