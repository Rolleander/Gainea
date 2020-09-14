package com.broll.gainea.server.core.map.impl;

import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.client.render.ExpansionRender;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.ContinentID;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.IslandID;

import java.util.List;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Areas.*;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Islands.*;
import static com.broll.gainea.server.core.map.impl.IcelandMap.Continents.*;

public class IcelandMap extends ExpansionFactory {
    public enum Continents implements ContinentID {

    }

    public enum Islands implements IslandID {

    }

    public enum Areas implements AreaID {
    }

    public IcelandMap() {
        super(ExpansionType.ICELANDS);
        setBaseCoordinates(-0.82f, 0.235f);
    }

    @Override
    public ExpansionRender createRender() {
        return new ExpansionRender("expansion_1.png");
    }

    @Override
    protected void init() {

    }

    @Override
    protected void connectWithExpansion(ExpansionFactory expansion) {

    }

}
