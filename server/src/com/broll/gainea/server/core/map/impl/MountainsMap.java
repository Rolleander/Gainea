package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;

public class MountainsMap extends ExpansionFactory {
    public enum Continents implements ContinentID {

    }

    public enum Islands implements IslandID {

    }

    public enum Areas implements AreaID {
    }

    public MountainsMap() {
        super(ExpansionType.MOUNTAINS);
        setBaseCoordinates(-1.05f,1.08f);
    }

    @Override
    public String getTexture() {
        return "expansion_4.png";
    }
    @Override
    protected void init() {

    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {

    }


}
