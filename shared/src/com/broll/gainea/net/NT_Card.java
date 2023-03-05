package com.broll.gainea.net;

import java.util.Objects;

public class NT_Card {

    public short id;
    public short picture;
    public String title, text;

    public boolean playable = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NT_Card nt_card = (NT_Card) o;
        return id == nt_card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
