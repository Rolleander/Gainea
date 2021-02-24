package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractBuffableValue<T, B extends AbstractBuff, V> {

    private T object;
    protected V value;
    protected V buffedValue;
    protected Set<B> buffs = new HashSet<>();

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
        recalc();
    }

    public V getValue() {
        return buffedValue;
    }

    public V getRootValue() {
        return value;
    }
}
