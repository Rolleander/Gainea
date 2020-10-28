package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public class G_AreaTypes extends AbstractGoal {

    private final static AreaType[] TYPES = new AreaType[]{AreaType.PLAINS, AreaType.DESERT, AreaType.LAKE, AreaType.MOUNTAIN};

    public G_AreaTypes() {
        super(GoalDifficulty.MEDIUM, "Erobere zwei beliebige Steppen, Wüsten, Meere und Berge");
        setExpansionRestriction(ExpansionType.GAINEA);
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        check();
    }

    @Override
    public void check() {
        int count = 0;
        List<Location> locations = LocationUtils.getControlledLocationsIn(player, ExpansionType.GAINEA);
        for (AreaType type : TYPES) {
            count += LocationUtils.filterByType(locations, type).count();
        }
        if (count >= TYPES.length * 2) {
            success();
        }
    }
}
