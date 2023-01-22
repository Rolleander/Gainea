package com.broll.gainea.server.core.objects.buffs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Buff<T extends BuffableValue> {

    private Set<T> buffedValues = new HashSet<>();

    void register(T buffableValue) {
        this.buffedValues.add(buffableValue);
    }

    public List<Object> getAffectedObjects() {
        return buffedValues.stream().map(BuffableValue::getObject).distinct().collect(Collectors.toList());
    }

    public void remove() {
        this.buffedValues.forEach(it -> it.clearBuff(this));
    }

}
