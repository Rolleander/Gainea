package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.broll.gainea.Gainea;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Location;

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
        this.setBounds(coords.getDisplayX()-SIZE/2, coords.getDisplayY()-SIZE/2, SIZE, SIZE);
    }

    public Expansion getExpansion() {
        return expansion;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(),getY());
        //   batch.end();
        //  render(game.shapeRenderer);
        //  batch.begin();
    }

//    public void render(ShapeRenderer shape) {
//        expansion.getContents().forEach(content -> {
//            content.getAreas().forEach(area -> renderArea(area, shape));
//            content.getShips().forEach(ship -> renderShip(ship, shape));
//        });
//    }
//
//    private void renderArea(Area location, ShapeRenderer shape) {
//        float x = this.getX() - SIZE / 2 + location.getCoordinates().getX(SIZE);
//        float y = this.getY() + SIZE / 2 - location.getCoordinates().getY(SIZE);
//        float r = 25;
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.setColor(Color.RED);
//        shape.circle(x, y, r);
//        shape.end();
//        renderConnections(location, location.getConnectedLocations(), shape);
//    }
//
//    private void renderShip(Ship location, ShapeRenderer shape) {
//        float x = this.getX() - SIZE / 2 + location.getCoordinates().getX(SIZE);
//        float y = this.getY() + SIZE / 2 - location.getCoordinates().getY(SIZE);
//        float r = 25;
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.setColor(Color.BROWN);
//        shape.circle(x, y, r);
//        shape.end();
//        List<Location> cons = new ArrayList<>();
//        cons.add(location.getFrom());
//        cons.add(location.getTo());
//        renderConnections(location, cons, shape);
//    }
//
//    private void renderConnections(Location location, Collection<Location> connections, ShapeRenderer shape) {
//        float x = this.getX() - SIZE / 2 + location.getCoordinates().getX(SIZE);
//        float y = this.getY() + SIZE / 2 - location.getCoordinates().getY(SIZE);
//        connections.forEach(loc -> {
//            float tx = this.getX() - SIZE / 2 + loc.getCoordinates().getX(SIZE);
//            float ty = this.getY() + SIZE / 2 - loc.getCoordinates().getY(SIZE);
//            shape.begin(ShapeRenderer.ShapeType.Line);
//            shape.setColor(Color.BLACK);
//            shape.line(x, y, tx, ty);
//            shape.end();
//        });
//    }

}
