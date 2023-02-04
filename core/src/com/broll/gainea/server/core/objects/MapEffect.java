package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_Event_BoardEffect;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.GameUtils;

import java.util.Arrays;

public class MapEffect {

    private float x, y;
    private String info;
    private int type;
    private int id;

    public MapEffect(int type, String info, Location location) {
        this.type = type;
        this.info = info;
        this.place(location);
    }

    public static void spawn(GameContainer game, MapEffect... effects){
        for(MapEffect effect : effects){
            effect.add(game);
        }
        NT_Event_BoardEffect nt = new NT_Event_BoardEffect();
        nt.effects = Arrays.stream(effects).map(MapEffect::nt).toArray(NT_BoardEffect[]::new);
        GameUtils.sendUpdate(game, nt);
    }

    public static void despawn(GameContainer game, MapEffect... effects){
        for(MapEffect effect : effects){
            effect.remove(game);
        }
        GameUtils.sendUpdate(game, game.nt());
    }

    public void add(GameContainer game) {
        this.id = game.newObjectId();
        game.getEffects().add(this);
    }

    public void remove(GameContainer game){
        game.getEffects().remove(this);
    }

    public void place(Location location) {
        this.x = location.getCoordinates().getDisplayX();
        this.y = location.getCoordinates().getDisplayY();
    }

    public NT_BoardEffect nt() {
        NT_BoardEffect nt = new NT_BoardEffect();
        nt.id = id;
        nt.effect = type;
        nt.info = info;
        nt.x = x;
        nt.y = y;
        return nt;
    }
}
