package com.broll.gainea.server.core.map;

import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.map.impl.IcelandMap;
import com.broll.gainea.server.core.map.impl.MountainsMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapFactory {

    public static List<Expansion> create(ExpansionSetting settings) {
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
        return expansions.stream().map(ExpansionFactory::create).collect(Collectors.toList());
    }
}
