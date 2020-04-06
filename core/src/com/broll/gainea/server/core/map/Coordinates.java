package com.broll.gainea.server.core.map;

import java.awt.geom.Point2D;

public class Coordinates {

    private float x;
    private float y;

    private float sx,sy;

    public Coordinates(float x, float y) {
        this.x=x;
        this.y=y;
    }

    public void shift(float x, float y){
        this.sx+=x;
        this.sy+=y;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }

    public float getX(float scale) {
        return (sx+x)*scale;
    }

    public float getY(float scale) {
        return (sy+y)*scale;
    }
}
