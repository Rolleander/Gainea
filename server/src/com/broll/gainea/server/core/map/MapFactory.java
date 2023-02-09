package com.broll.gainea.server.core.map;

import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;
import com.broll.gainea.server.init.ExpansionSetting;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MapFactory {


    public final static float SIZE = 3120;

    public static List<Pair<ExpansionFactory, Expansion>> create(ExpansionSetting settings) {
        List<ExpansionFactory> expansions = new ArrayList<>();
        for (ExpansionType type : settings.getMaps()) {
            switch (type) {
                case GAINEA:
                    expansions.add(new GaineaMap());
                    break;
                case ICELANDS:
                    expansions.add(new IcelandMap());
                    break;
                case BOGLANDS:
                    expansions.add(new BoglandMap());
                    break;
                case MOUNTAINS:
                    expansions.add(new MountainsMap());
                    break;
            }
        }
        List<Pair<ExpansionFactory, Expansion>> set = expansions.stream().map(it -> Pair.of(it, it.create())).collect(Collectors.toList());
        expansions.forEach(map1 -> {
            expansions.forEach(map2 -> {
                if (map1 != map2) {
                    map1.connectExpansion(map2);
                }
            });
        });
        List<Expansion> maps = set.stream().map(it -> it.getRight()).collect(Collectors.toList());
        AtomicInteger locationNumer = new AtomicInteger(0);
        maps.stream().flatMap(it -> it.getAllLocations().stream()).sorted((l1, l2) -> l1.getCoordinates().compareTo(l2.getCoordinates())).forEach(it -> it.setNumber(locationNumer.getAndIncrement()));
        set.forEach(it -> {
            it.getRight().getCoordinates().calcDisplayLocation(SIZE);
            it.getRight().getAllLocations().stream().map(Location::getCoordinates).forEach(coords -> {
                coords.shift(-0.5f, 0);
                coords.mirrorY(0.5f);
                coords.calcDisplayLocation(SIZE);
            });
        });
        return set;
    }

    public static List<Expansion> createRenderless(ExpansionSetting settings) {
        return create(settings).stream().map(it -> it.getRight()).collect(Collectors.toList());
    }
}
