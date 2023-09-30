package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.ExpansionDebugRender;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.client.ui.ingame.map.WaterRender;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionFactory;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.map.MapFactory;
import com.broll.gainea.server.init.ExpansionSetting;

import java.util.List;
import java.util.stream.Collectors;

import kotlin.Pair;

public class ClientMapContainer extends MapContainer {
    public static boolean RENDER_DEBUG = false;
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
            ExpansionRender render = new ExpansionRender(it.getFirst().getTexture());
            render.init(game, it.getSecond());
            return render;
        }).collect(Collectors.toList());
    }

    public List<ExpansionRender> getRenders() {
        return renders;
    }

    @Override
    protected void init(ExpansionSetting setting) {
        this.initSet = MapFactory.create(setting);
        this.getExpansions().addAll(initSet.stream().map(Pair::getSecond).collect(Collectors.toList()));
    }

    public void displayRenders() {
        game.gameStage.addActor(new WaterRender(game));
        this.renders.forEach(render -> game.gameStage.addActor(render));
        if (RENDER_DEBUG) {
            this.getExpansions().forEach(expansion -> game.gameStage.addActor(new ExpansionDebugRender(game, expansion)));
        }
    }

}
