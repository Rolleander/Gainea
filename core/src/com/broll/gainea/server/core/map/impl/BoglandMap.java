package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;

import java.util.List;

public class BoglandMap extends ExpansionFactory {
    public BoglandMap() {
        super(ExpansionType.BOGLANDS);
        setBaseCoordinates(-0.2f,0.9f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_2.png");
    }

    @Override
    protected void init(List<AreaCollection> contents) {

    }

    @Override
    protected void connectExpansion(ExpansionFactory expansion) {

    }
}
