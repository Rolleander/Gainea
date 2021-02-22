package com.broll.gainea.server.core.objects.buffs;

public class IntBuff extends AbstractBuff<BuffableInt> {

    private BuffType type;
    private int modifier;

    public IntBuff(BuffType type, int modifier) {
        this.type = type;
        this.modifier = modifier;
    }

    public BuffType getType() {
        return type;
    }

    public int getModifier() {
        return modifier;
    }
}
