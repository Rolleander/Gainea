package com.broll.gainea.server.core.objects.buffs;


public class BuffableInt<T> extends BuffableValue<T, IntBuff, Integer> {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    public BuffableInt(T object, int value) {
        super(object);
        this.value = value;
        recalc();
    }

    public BuffableInt copy(T object) {
        BuffableInt copy = new BuffableInt(object, value);
        copy.minValue = this.minValue;
        copy.maxValue = this.maxValue;
        copy.buffs = this.buffs;
        return copy;
    }

    protected void recalc() {
        buffedValue = value.intValue();
        for (IntBuff buff : buffs) {
            int modifier = buff.getModifier();
            switch (buff.getType()) {
                case ADD:
                    buffedValue += modifier;
                    break;
                case MULTIPLY:
                    buffedValue *= modifier;
                    break;
                case SET:
                    buffedValue = modifier;
                    break;
            }
        }
        if (buffedValue < minValue) {
            buffedValue = minValue;
        } else if (buffedValue > maxValue) {
            buffedValue = maxValue;
        }
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        recalc();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        recalc();
    }

    public void addValue(int value) {
        this.value += value;
        recalc();
    }


}
