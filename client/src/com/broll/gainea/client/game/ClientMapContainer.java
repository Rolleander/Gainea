package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.ExpansionDebugRender;
import com.broll.gainea.client.ui.ingame.map.ExpansionRender;
import com.broll.gainea.client.ui.ingame.map.MapLabel;
import com.broll.gainea.client.ui.ingame.map.WaterRender;
import com.broll.gainea.core.map.ExpansionSetting;

import java.util.List;
import java.util.stream.Collectors;

public class ClientMapContainer {
    public static boolean RENDER_DEBUG = false;
    private List<ExpansionRender> renders;
    private Gainea game;
    private List<MapFactory.ExpansionData> initSet;
    private List<Expansion>

    public ClientMapContainer(Gainea game, ExpansionSetting setting) {
        this.game = game;
        initRenders(game, setting);
    }

    private void initRenders(Gainea game, ExpansionSetting setting) {
        this.renders = initSet.stream().map(it -> {
            ExpansionRender render = new ExpansionRender(it.getTexture());
            render.init(game, it.getExpansion());
            return render;
        }).collect(Collectors.toList());
    }

    public List<ExpansionRender> getRenders() {
        return renders;
    }

    protected void init(ExpansionSetting setting) {
        this.initSet = MapFactory.create(setting);
        this.getExpansions().addAll(initSet.stream().map(MapFactory.ExpansionData::getExpansion).collect(Collectors.toList()));
    }

    public void displayRenders() {
        game.gameStage.addActor(new WaterRender(game));
        this.renders.forEach(render -> game.gameStage.addActor(render));
        this.initSet.forEach(it -> {
            it.getExpansion().getAllAreas().forEach(area -> {
                MapLabel label = new MapLabel(game, area.getName());
                Coordinates cords = area.getCoordinates();
                label.setPosition(cords.getDisplayX(), cords.getDisplayY());
                game.gameStage.addActor(label);
            });
        });
        if (RENDER_DEBUG) {
            this.getExpansions().forEach(expansion -> game.gameStage.addActor(new ExpansionDebugRender(game, expansion)));
        }
    }

}
