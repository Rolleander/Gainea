package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.objects.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBuff<T extends AbstractBuffableValue> {

    private List<T> buffedValues = new ArrayList<>();

    void register(T buffableValue) {
        this.buffedValues.add(buffableValue);
    }

    public List<Object> getAffectedObjects() {
        return buffedValues.stream().map(AbstractBuffableValue::getObject).distinct().collect(Collectors.toList());
    }

    public void remove() {
        this.buffedValues.forEach(it -> it.clearBuff(this));
    }

}
