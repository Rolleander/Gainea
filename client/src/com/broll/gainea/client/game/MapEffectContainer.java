package com.broll.gainea.client.game;

import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.map.MapEffectRender;
import com.broll.gainea.net.NT_BoardEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapEffectContainer {

    private Gainea game;
    private Map<Integer, MapEffectRender> renders = new HashMap<>();

    public MapEffectContainer(Gainea game) {
        this.game = game;
    }

    public void update(List<NT_BoardEffect> effects) {
        for (NT_BoardEffect effect : effects) {
            MapEffectRender render = renders.get(effect.id);
            if (render == null) {
                render = new MapEffectRender(game, effect);
                game.gameStage.addActor(render);
                renders.put(effect.id, render);
            } else {
                render.setPosition(effect.x, effect.y);
            }
        }
        if (renders.size() > effects.size()) {
            renders.keySet().stream().filter(key -> effects.stream().map(effect -> effect.id).noneMatch(id -> id == key))
                    .forEach(oldKey -> {
                        MapEffectRender oldValue = renders.remove(oldKey);
                        oldValue.remove();
                    });
        }
        game.gameStage.sort();
    }
}
