package com.broll.gainea.server.core.goals.impl.e3;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class G_IcePlates extends CustomOccupyGoal {
    private final static int COUNT = 3;

    public G_IcePlates() {
        super(GoalDifficulty.EASY, "Besetze alle Eisgebiete mit jeweils " + COUNT + " Einheiten");
        setExpansionRestriction(ExpansionType.BOGLANDS);
    }

    @Override
    public void check() {
        int playerUnits = game.getMap().getExpansion(ExpansionType.BOGLANDS).getAllAreas().stream()
                .filter(it -> LocationUtils.isAreaType(it, AreaType.SNOW))
                .map(it -> Math.min(COUNT, PlayerUtils.getUnits(player, it).size())).reduce(0, Integer::sum);
        updateProgression(playerUnits);
        if (playerUnits >= COUNT) {
            success();
        }
    }

}
