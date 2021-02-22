package com.broll.gainea.server.core.objects.buffs;


public class BuffableBoolean<T> extends AbstractBuffableValue<T, BooleanBuff, Boolean> {

    public BuffableBoolean(T object, boolean value) {
        super(object);
        this.value = value;
        recalc();
    }

    protected void recalc() {
        buffedValue = value.booleanValue();
        for (BooleanBuff buff : buffs) {
            buffedValue = buff.getModifier();
        }
    }

}
