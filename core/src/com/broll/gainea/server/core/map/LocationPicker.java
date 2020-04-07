package com.broll.gainea.server.core.map;

import java.util.List;

import com.broll.gainea.server.core.GameContainer;

import java.util.Collections;
import java.util.stream.Collectors;

public class LocationPicker {

    public static Area pickRandom(MapContainer map) {
        return pickRandom(map.getAllAreas(), true);
    }

    public static List<Area> pickRandom(MapContainer map, int amount) {
        List<Area> areas = map.getAllAreas();
        Collections.shuffle(areas);
        return areas.stream().limit(amount).collect(Collectors.toList());
    }

    public static Area pickRandom(List<Area> areas, boolean mustBeFree) {
        do {
            Area random = areas.get((int) (Math.random() * areas.size()));
            if(mustBeFree){
                if(random.getInhabitants().isEmpty()){
                    return random;
                }
            }
            else{
                return random;
            }
        } while (true);
    }
}
