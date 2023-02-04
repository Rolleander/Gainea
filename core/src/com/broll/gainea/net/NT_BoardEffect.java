package com.broll.gainea.net;

import java.util.Objects;

public class NT_BoardEffect {

    final public static int EFFECT_FIRE=0;
    public int effect;
    public int id;
    public String info;
    public float x,y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_BoardEffect that = (NT_BoardEffect) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
