package com.broll.gainea.client.game;

import com.badlogic.gdx.math.Vector2;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.ExpansionDebugRender;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.client.ui.ingame.map.WaterRender;
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
    public static boolean RENDER_DEBUG = false;

    public ClientMapContainer(Gainea game, ExpansionSetting setting) {
        super(setting);
        this.game = game;
        initRenders(game, setting);
    }

    private void initRenders(Gainea game, ExpansionSetting setting) {
        this.renders = initSet.stream().map(it -> {
            ExpansionRender render = new ExpansionRender(it.getLeft().texture);
            render.init(game, it.getRight());
            return render;
        }).collect(Collectors.toList());
    }

    public List<ExpansionRender> getRenders() {
        return renders;
    }

    @Override
    protected void init(ExpansionSetting setting) {
        this.initSet = MapFactory.create(setting);
        this.expansions = initSet.stream().map(it -> it.getRight()).collect(Collectors.toList());
    }

    public void displayRenders() {
        game.gameStage.addActor(new WaterRender(game));
        this.renders.forEach(render -> game.gameStage.addActor(render));
        if (RENDER_DEBUG) {
            this.expansions.forEach(expansion -> game.gameStage.addActor(new ExpansionDebugRender(game, expansion)));
        }
    }

}
