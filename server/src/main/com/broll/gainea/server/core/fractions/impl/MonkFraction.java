package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.UnitControl;

import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;
import java.util.stream.Collectors;

public class MonkFraction extends Fraction {
    private int killedMonsters = 0;

    public MonkFraction() {
        super(FractionType.MONKS);
    }


    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Jede Runde erhält eine Einheit +1 Leben");
        desc.plus("Werden nicht von Monstern angegriffen");
        desc.contra("Jedes dritte besiegte Monster gibt keine Belohnung");
        return desc;
    }

    @Override
    public void battleIntention(BattleContext context, MutableBoolean cancelFight) {
        List<Unit> neutralMonsters = context.getAttackers().stream().filter(it -> it.getOwner() == null && it instanceof Monster).collect(Collectors.toList());
        List<Unit> defendingMonks = context.getDefenders().stream().filter(it -> it instanceof MonkSoldier).collect(Collectors.toList());
        if (!neutralMonsters.isEmpty() && !defendingMonks.isEmpty()) {
            cancelFight.setTrue();
            //move monsters anyway
            UnitControl.move(game, neutralMonsters, context.getLocation());
        }
    }

    @Override
    public void killedMonster(Monster monster) {
        killedMonsters++;
        if (killedMonsters == 3) {
            killedMonsters = 0;
            return;
        }
        super.killedMonster(monster);
    }

    @Override
    public void turnStarted(ActionHandlers actionHandlers) {
        Unit randomUnit = RandomUtils.pickRandom(owner.getUnits());
        if (randomUnit != null) {
            //inc life
            randomUnit.getMaxHealth().addValue(1);
            //heal
            UnitControl.heal(game, randomUnit, 1);
        }
        super.turnStarted(actionHandlers);
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new MonkSoldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Mönch");
        soldier.setIcon(108);
        return soldier;
    }


    @Override
    public Soldier createCommander() {
        Soldier commander = new MonkSoldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Großmeister Eron");
        commander.setIcon(107);
        return commander;
    }

    private static class MonkSoldier extends Soldier {

        public MonkSoldier(Player owner) {
            super(owner);
        }
    }
}
