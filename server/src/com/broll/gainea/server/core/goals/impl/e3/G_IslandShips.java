package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.map.Expansion;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.BoglandMap;
import com.broll.gainea.server.core.utils.ShipUtils;

import java.util.ArrayList;
import java.util.List;

public class G_IslandShips extends OccupyGoal {
    public G_IslandShips() {
        super(GoalDifficulty.EASY, "Besetze alle Schiffe die um die obere und untere Insel führen");
    }

    @Override
    protected void initOccupations() {
        Expansion expansion = game.getMap().getExpansion(ExpansionType.BOGLANDS);
        if (expansion != null) {
            Island kleinspalt = expansion.getIsland(BoglandMap.Islands.KLEINSPALT);
            List<Location> ships = new ArrayList<>();
            ships.addAll( ShipUtils.getAllShips(kleinspalt));
            occupy( ships);
        }
    }

}
