package com.broll.gainea.net;

import java.util.Objects;

public class NT_BoardObject {

    public final static int NO_LOCATION = -1;
    public String name;
    public float size;
    public int icon;
    public int id;
    public int location = NO_LOCATION;
    public float x, y;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_BoardObject that = (NT_BoardObject) o;
        return id == that.id;
    }

}
