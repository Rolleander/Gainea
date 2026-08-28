package com.broll.gainea.net;

public abstract class NT_Event {

    public final static int EFFECT_HEAL = 1;
    public final static int EFFECT_DAMAGE = 2;
    public final static int EFFECT_BUFF = 3;
    public final static int EFFECT_DEBUFF = 4;

    public final static int EFFECT_SPAWNED = 5;
    public final static int EFFECT_MOVED = 6;

    public int screenEffect;
    public int player;
    public String sound;
}
