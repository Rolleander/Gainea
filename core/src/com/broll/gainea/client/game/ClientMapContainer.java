package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.map.MapFactory;
import com.broll.gainea.server.init.ExpansionSetting;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

public class ClientMapContainer extends MapContainer {
    private List<ExpansionRender> renders;
    private Gainea game;
    private List<Pair<ExpansionFactory, Expansion>> initSet;

    public ClientMapContainer(Gainea game, ExpansionSetting setting) {
        super(setting);
        this.game = game;
        initRenders(game, setting);
    }

    private void initRenders(Gainea game, ExpansionSetting setting) {
        this.renders = initSet.stream().map(it -> {
            ExpansionRender render = it.getLeft().createRender();
            render.init(game, it.getRight());
            return render;
        }).collect(Collectors.toList());
    }

    @Override
    protected void init(ExpansionSetting setting) {
        this.initSet = MapFactory.create(setting);
        this.expansions = initSet.stream().map(it -> it.getRight()).collect(Collectors.toList());
    }

    public List<ExpansionRender> getRenders() {
        return renders;
    }
}
