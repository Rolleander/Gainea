package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;

public class G_GaineaKillAnimals extends Goal {
    public G_GaineaKillAnimals() {
        super(GoalDifficulty.MEDIUM, "Befreie den Kontinent Gainea von allen Monstern");
        setExpansionRestriction(ExpansionType.GAINEA);
    }

    @Override
    public void check() {
        if (game.getObjects().stream().noneMatch(it -> it instanceof Monster &&
                LocationUtils.isInContinent(it.getLocation(), GaineaMap.Continents.GAINEA))) {
            success();
        }
    }

    @Override
    public void earnedStars(Player player, int stars) {
        check();
    }

}
