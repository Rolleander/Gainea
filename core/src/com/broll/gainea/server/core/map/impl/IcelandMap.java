package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;

import java.util.List;

public class IcelandMap extends ExpansionFactory {
    public IcelandMap() {
        super(ExpansionType.ICELANDS);
        setBaseCoordinates(-0.74f,0.235f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_1.png");
    }

    @Override
    protected void init(List<AreaCollection> contents) {

    }

    @Override
    protected void connectExpansion(ExpansionFactory expansion) {

    }
}
