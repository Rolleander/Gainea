package com.broll.gainea.net;

public abstract class NT_Abstract_Event {

    public final static int EFFECT_HEAL = 1;
    public final static int EFFECT_DAMAGE = 2;
    public final static int EFFECT_BUFF = 3;

    public int screenEffect;
    public int player;
    public String sound;
}
