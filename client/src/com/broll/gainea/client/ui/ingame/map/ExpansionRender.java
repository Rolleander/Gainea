package com.broll.gainea.client.ui.ingame.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.DepthActor;
import com.broll.gainea.server.core.map.Coordinates;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.Ship;

public class ExpansionRender extends DepthActor {

    public final static float SIZE = 3120;
    private final static int SHIP_SIZE = 120;
    private final static int SHIP_D = 15;
    private String textureName;
    private Texture texture;
    private Expansion expansion;
    private Gainea game;
    private TextureRegion shipTexture;
    private float shipAnimation;


    public ExpansionRender(String textureName) {
        this.textureName = textureName;
    }

    public void init(Gainea game, Expansion expansion) {
        depth = -100;
        this.game = game;
        this.expansion = expansion;
        texture = game.assets.get("textures/" + textureName, Texture.class);
        shipTexture = new TextureRegion(game.assets.get("textures/ship.png", Texture.class));
        Coordinates coords = expansion.getCoordinates();
        this.setBounds(coords.getDisplayX() - SIZE / 2, coords.getDisplayY() - SIZE / 2, SIZE, SIZE);
    }

    public Expansion getExpansion() {
        return expansion;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        shipAnimation += Gdx.graphics.getDeltaTime();
        batch.draw(texture, getX(), getY());
        expansion.getAllShips().forEach(ship -> drawShip(batch, ship));
    }

    private void drawShip(Batch batch, Ship ship) {
        float x = ship.getCoordinates().getDisplayX();
        float y = ship.getCoordinates().getDisplayY();
        float tx = ship.getTo().getCoordinates().getDisplayX();
        float ty = ship.getTo().getCoordinates().getDisplayY();
        float fx = ship.getFrom().getCoordinates().getDisplayX();
        float fy = ship.getFrom().getCoordinates().getDisplayY();
        float angleTo = (float) Math.toDegrees(MathUtils.atan2(ty - y, tx - x)) - 90;
        float angleFrom = (float) Math.toDegrees(MathUtils.atan2(y - fy, x - fx)) - 90;
        float diff = ((angleTo - angleFrom + 180 + 360) % 360) - 180;
        float angle = (360 + angleFrom + (diff / 2)) % 360;
        //TextureRegion region, float x, float y, float originX, float originY, float width, float height,
        //		float scaleX, float scaleY, float rotation
        //        batch.draw();
        //
        float hop = (float) (Math.sin(shipAnimation + ship.getNumber()) * 5);

        batch.draw(shipTexture, x - SHIP_SIZE / 2, y - SHIP_SIZE / 2 + SHIP_D + hop, SHIP_SIZE / 2, SHIP_SIZE / 2 - SHIP_D, SHIP_SIZE, SHIP_SIZE, 1, 1, angle);
//        batch.draw(shipTexture, x -100, y-100);
    }

}
