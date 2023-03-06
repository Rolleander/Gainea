package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.buffs.BuffableInt;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;

public abstract class MapObject extends GameUpdateReceiverAdapter {
    protected GameContainer game;

    protected Player owner;

    private Location location;

    private int id;

    private int icon = 0;

    private String name;

    private float scale = 1;

    protected int moveCount;

    protected BuffableInt<MapObject> movesPerTurn = new BuffableInt<>(this, 1); //default 1 move

    public void init(GameContainer game) {
        this.id = game.newObjectId();
        this.game = game;
    }

    public void turnStart() {
        moveCount = 0;
    }

    public boolean hasRemainingMove() {
        return moveCount < movesPerTurn.getValue();
    }

    public void moved() {
        moveCount++;
    }

    public BuffableInt<MapObject> getMovesPerTurn() {
        return movesPerTurn;
    }

    public Location getLocation() {
        return location;
    }

    public MapObject() {
        this(null);
    }

    public MapObject(Player owner) {
        this.owner = owner;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScale() {
        return scale;
    }

    public int getId() {
        return id;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public boolean canMoveTo(Location to) {
        if (!to.isTraversable()) {
            return false;
        }
        if (to instanceof Ship) {
            if (!((Ship) to).passable(location)) {
                return false;
            }
        }
        if (location instanceof Ship) {
            if (((Ship) location).getTo() != to) {
                return false;
            }
        }
        return true;
    }

    public NT_BoardObject nt() {
        NT_BoardObject object = new NT_BoardObject();
        fillObject(object);
        return object;
    }

    protected void fillObject(NT_BoardObject object) {
        object.id = (short) id;
        object.name = name;
        object.icon = (short) icon;
        object.size = scale * 30;
        if (location != null) {
            object.location = (short) location.getNumber();
        }
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "MapObject{" +
                "id=" + id +
                ", name='" + name +
                ", location=" + location + '\'' +
                '}';
    }
}
