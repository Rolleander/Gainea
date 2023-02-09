package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class PoacherFraction extends Fraction {
    public PoacherFraction() {
        super(FractionType.POACHER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Solange Kommandant lebt werden besiegte wilde Monster rekrutiert");
        desc.contra("Gegen menschliche Truppen -2 Würfel");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if (enemies.stream().map(it -> it instanceof Monster == false).reduce(true, Boolean::logicalAnd)) {
            power.changeDiceNumber(-2);
        }
        return power;
    }

    @Override
    public void killedMonster(Monster monster) {
        super.killedMonster(monster);
        if (PlayerUtils.isCommanderAlive(owner)) {
            //recruit monster in player army
            Monster recruited = new Monster();
            BattleObject.copy(monster, recruited);
            recruited.heal();
            recruited.setOwner(owner);
            UnitControl.spawn(game, recruited, recruited.getLocation());
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Wilderer");
        soldier.setIcon(42);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Monsterzähmer");
        commander.setIcon(44);
        commander.setStats(1, 5);
    }


}
