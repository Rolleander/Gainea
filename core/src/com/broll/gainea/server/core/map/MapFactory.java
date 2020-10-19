package com.broll.gainea.server.core.map;

import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MapFactory {

    public static List<Pair<ExpansionFactory, Expansion>> create(ExpansionSetting settings) {
        List<ExpansionFactory> expansions = new ArrayList<>();
        switch (settings) {
            case FULL:
                expansions.add(new MountainsMap());
            case PLUS_ICELANDS_AND_BOG:
                expansions.add(new BoglandMap());
            case PLUS_ICELANDS:
                expansions.add(new IcelandMap());
            case BASIC_GAME:
                expansions.add(new GaineaMap());
        }
        expansions.forEach(map1 -> {
            expansions.forEach(map2 -> {
                if (map1 != map2) {
                    map1.connectExpansion(map2);
                }
            });
        });
        List<Pair<ExpansionFactory, Expansion>> set = expansions.stream().map(it -> Pair.of(it, it.create())).collect(Collectors.toList());
        List<Expansion> maps = set.stream().map(it -> it.getRight()).collect(Collectors.toList());
        AtomicInteger locationNumer = new AtomicInteger(0);
        maps.stream().flatMap(it -> it.getAllLocations().stream()).sorted((l1, l2) -> l1.getCoordinates().compareTo(l2.getCoordinates())).forEach(it -> it.setNumber(locationNumer.getAndIncrement()));
        return set;
    }

    public static List<Expansion> createRenderless(ExpansionSetting settings) {
        return create(settings).stream().map(it -> it.getRight()).collect(Collectors.toList());
    }
}
