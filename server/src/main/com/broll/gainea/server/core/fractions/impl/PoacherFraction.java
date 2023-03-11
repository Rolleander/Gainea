package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.UnitControl;

public class PoacherFraction extends Fraction {
    public PoacherFraction() {
        super(FractionType.POACHER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Besiegte Monster werden rekrutiert");
        desc.contra("Gegen menschliche Truppen -1 Zahl für eigene Soldaten");
        return desc;
    }

    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        if (!context.isNeutralParticipant()) {
            power.changeNumberPlus(-1);
        }
        return power;
    }

    @Override
    public void killedMonster(Monster monster) {
        super.killedMonster(monster);
        //recruit monster in player army
        Monster recruited = new Monster();
        Unit.copy(monster, recruited);
        recruited.heal();
        recruited.setOwner(owner);
        UnitControl.spawn(game, recruited, recruited.getLocation());
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Wilderer");
        soldier.setIcon(42);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setName("Monsterzähmer");
        commander.setIcon(44);
        commander.setStats(1, 5);
        return commander;
    }

}
