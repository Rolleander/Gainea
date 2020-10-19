package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.client.ui.elements.render.ExpansionRender;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;

public class BoglandMap extends ExpansionFactory {

    public enum Continents implements ContinentID {

    }

    public enum Islands implements IslandID {

    }

    public enum Areas implements AreaID {
    }

    public BoglandMap() {
        super(ExpansionType.BOGLANDS);
        setBaseCoordinates(-0.25f,0.9f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_2.png");
    }

    @Override
    protected void init() {

    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {

    }


}
