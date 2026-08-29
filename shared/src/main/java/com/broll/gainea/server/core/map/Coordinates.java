package com.broll.gainea.server.core.map;

public class Coordinates implements Comparable<Coordinates> {
    private float rootX;
    private float rootY;
    private float displayX;
    private float displayY;
    private float sx;
    private float sy;

    public Coordinates(float rootX, float rootY) { this.rootX = rootX; this.rootY = rootY; }
    public float getRootX() { return rootX; }
    public void setRootX(float rootX) { this.rootX = rootX; }
    public float getRootY() { return rootY; }
    public void setRootY(float rootY) { this.rootY = rootY; }
    public float getDisplayX() { return displayX; }
    public float getDisplayY() { return displayY; }
    public float getX() { return sx + rootX; }
    public float getY() { return sy + rootY; }
    public void shift(float x, float y) { sx += x; sy += y; }
    public void set(float x, float y) { rootX = x; rootY = y; }
    public void calcDisplayLocation(float size) { displayX = getX() * size; displayY = getY() * size; }
    public void setSx(float sx) { this.sx = sx; }
    public void setSy(float sy) { this.sy = sy; }
    public void mirrorY(float mirrorY) { rootY = mirrorY - rootY; }

    @Override public int compareTo(Coordinates other) {
        int compare = Float.compare(rootY, other.rootY);
        return compare != 0 ? compare : Float.compare(rootX, other.rootX);
    }
}
