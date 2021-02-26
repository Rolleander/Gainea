package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.utils.ShipUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class G_GaineaShips extends AbstractOccupyGoal {
    public G_GaineaShips() {
        super(GoalDifficulty.MEDIUM, "Besetze alle Schiffe die von Moor, Zuba und der Vulkaninsel nach Gainea führen");
    }

    @Override
    protected void initOccupations() {
        Expansion expansion = game.getMap().getExpansion(ExpansionType.GAINEA);
        if (expansion != null) {
            Continent gainea = expansion.getContinent(GaineaMap.Continents.GAINEA);
            Continent moor = expansion.getContinent(GaineaMap.Continents.MOOR);
            Continent zuba = expansion.getContinent(GaineaMap.Continents.ZUBA);
            Island vulkanInsel = expansion.getIsland(GaineaMap.Islands.VULKANINSEL);
            List<Location> ships = new ArrayList<>();
            //TODO PROBLEM
            ships.addAll(ShipUtils.getShips(zuba, gainea));
            ships.addAll(ShipUtils.getShips(moor, gainea));
            ships.addAll(ShipUtils.getShips(vulkanInsel, gainea));
            occupy(ships);
        }
    }

}
