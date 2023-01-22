package com.broll.gainea.server.core.goals.impl.e1;

import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;

public class G_GaineaKillAnimals extends Goal {
    public G_GaineaKillAnimals() {
        super(GoalDifficulty.MEDIUM, "Befreie den Kontinent Gainea von allen Monstern");
        setExpansionRestriction(ExpansionType.GAINEA);
    }

    @Override
    public void check() {
        long monstersInGainea = game.getMap().getContinent(GaineaMap.Continents.GAINEA).getAreas().stream().flatMap(area -> area.getInhabitants().stream()).filter(it -> it instanceof Monster).count();
        if (monstersInGainea == 0) {
            success();
        }
    }

    @Override
    public void earnedStars(Player player, int stars) {
        check();
    }

}
