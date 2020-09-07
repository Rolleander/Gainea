package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.PlayerUtils;

public class NomadsFraction extends Fraction {
    public NomadsFraction(FractionType type) {
        super(FractionType.NOMADS);
    }

    @Override
    public void killedMonster(Monster monster) {
        super.killedMonster(monster);
        if (PlayerUtils.isCommanderAlive(owner)) {
            //recruit monster in player army
            monster.heal();
            monster.getLocation().getInhabitants().add(monster);
            owner.getUnits().add(monster);
        }
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        return desc;
    }
}
