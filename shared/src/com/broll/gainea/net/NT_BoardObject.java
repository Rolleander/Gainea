package com.broll.gainea.net;

import java.util.Objects;

public class NT_BoardObject {

    public final static short NO_LOCATION = -1;
    public final static byte NO_OWNER = -1;
    public short owner = NO_OWNER;

    public String name;
    public float scale = 1;
    public short icon;
    public short id;
    public String description;
    public short location = NO_LOCATION;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_BoardObject that = (NT_BoardObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
