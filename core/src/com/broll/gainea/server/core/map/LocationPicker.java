package com.broll.gainea.server.core.map;

import java.util.List;

import com.broll.gainea.server.core.GameContainer;

import java.util.Collections;
import java.util.stream.Collectors;

public class LocationPicker {

    public static List<Area> pickRandom(MapContainer map, int amount) {
        List<Area> areas = map.getAllAreas();
        Collections.shuffle(areas);
        return areas.stream().limit(amount).collect(Collectors.toList());
    }

    public static List<Area> pickRandomEmpty(MapContainer map, int amount) {
        List<Area> areas = map.getAllAreas();
        Collections.shuffle(areas);
        return areas.stream().filter(it -> it.getInhabitants().isEmpty()).limit(amount).collect(Collectors.toList());
    }

}
