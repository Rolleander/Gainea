package com.broll.gainea.server.core.objects.buffs;

public class BooleanBuff extends Buff<BuffableInt> {

    private boolean modifier;

    public BooleanBuff( boolean modifier) {
        this.modifier = modifier;
    }

    public boolean getModifier() {
        return modifier;
    }
}
