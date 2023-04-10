package com.broll.gainea.server.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.MapContainer;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.init.ExpansionSetting;

import org.junit.jupiter.api.Test;

public class PathFindingTests {

    @Test
    public void test() {
        MapContainer map = new MapContainer(ExpansionSetting.BASIC_GAME);
        Location from = map.getArea(GaineaMap.Areas.MOORWUESTE);
        Location to = map.getArea(GaineaMap.Areas.UFERLAND);
        Soldier soldier = new Soldier(null);
        assertEquals(12, LocationUtils.getWalkingDistance(soldier, from, to));
    }

}
