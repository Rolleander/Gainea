package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;

public abstract class MapObject {

    private Location location;

    private int id;

    private int icon = 0;

    private String name;

    private float scale = 1;

    public void init(GameContainer game) {
        this.id = game.newObjectId();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (this.location != null) {
            this.location.getInhabitants().remove(this);
        }
        this.location = location;
        this.location.getInhabitants().add(this);
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

    public NT_BoardObject nt() {
        NT_BoardObject object = new NT_BoardObject();
        fillObject(object);
        return object;
    }

    protected void fillObject(NT_BoardObject object) {
        object.id = id;
        object.name = name;
        object.icon = icon;
        object.size = scale * 30;
        object.location = location.getNumber();
    }
}
