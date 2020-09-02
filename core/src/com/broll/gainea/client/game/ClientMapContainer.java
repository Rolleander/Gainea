package com.broll.gainea.client.game;

import com.badlogic.gdx.maps.MapRenderer;
import com.broll.gainea.client.render.ExpansionRender;
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

    public ClientMapContainer(ExpansionSetting setting) {
        super(setting);
    }

    @Override
    protected void init(ExpansionSetting setting) {
        List<Pair<ExpansionFactory, Expansion>> set = MapFactory.create(setting);
        this.expansions = set.stream().map(it -> it.getRight()).collect(Collectors.toList());
        this.renders = set.stream().map(it -> {
            ExpansionRender render = it.getLeft().createRender();
            render.init(it.getRight());
            return render;
        }).collect(Collectors.toList());
    }

    public List<ExpansionRender> getRenders() {
        return renders;
    }
}
