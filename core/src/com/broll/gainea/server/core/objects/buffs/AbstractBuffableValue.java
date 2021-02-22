package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuffableValue<T, B extends AbstractBuff, V> {

    private T object;
    protected V value;
    protected V buffedValue;
    protected List<B> buffs = new ArrayList<>();

    public AbstractBuffableValue(T object) {
        this.object = object;
    }

    public void addBuff(B buff) {
        this.buffs.add(buff);
        buff.register(this);
        recalc();
    }

    void clearBuff(B buff) {
        this.buffs.remove(buff);
        recalc();
    }

    protected abstract void recalc();

    public void clearBuffs() {
        buffs.clear();
    }

    public T getObject() {
        return object;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public V getValue() {
        return buffedValue;
    }

    public V getRootValue() {
        return value;
    }
}
