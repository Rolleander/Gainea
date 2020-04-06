package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;

import java.util.List;

public class MountainsMap extends ExpansionFactory {
    public MountainsMap() {
        super(ExpansionType.MOUNTAINS);
        setBaseCoordinates(-1f,1.1f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_3.png");
    }

    @Override
    protected void init(List<AreaCollection> contents) {

    }

    @Override
    protected void connectExpansion(ExpansionFactory expansion) {

    }
}
