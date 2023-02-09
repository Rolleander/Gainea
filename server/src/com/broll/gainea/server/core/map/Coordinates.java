package com.broll.gainea.server.core.map;

import java.awt.geom.Point2D;

public class Coordinates implements Comparable<Coordinates> {

    private float x;
    private float y;
    private float displayX;
    private float displayY;

    private float sx, sy;

    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void shift(float x, float y) {
        this.sx += x;
        this.sy += y;
    }

    public void calcDisplayLocation(float size) {
        this.displayX = getX() * size;
        this.displayY = getY() * size;
    }

    public float getDisplayX() {
        return displayX;
    }

    public float getDisplayY() {
        return displayY;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }

    public float getX() {
        return (sx + x);
    }

    public float getY() {
        return (sy + y);
    }

    public void mirrorY(float mirrorY) {
        this.y = mirrorY - y;
    }

    @Override
    public int compareTo(Coordinates o) {
        int compare = Float.compare(y, o.y);
        if (compare != 0) {
            return compare;
        }
        return Float.compare(x, o.x);
    }
}
