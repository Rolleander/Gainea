package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.List;

public class WaterFraction extends Fraction {

    private final static int FROZEN_ROUNDS = 2;

    private List<BattleObject> spawns = new ArrayList<>();

    public WaterFraction() {
        super(FractionType.WATER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Fällt Arn wird er in eurem nächsten Zug als Eiskoloss (2/4) wiederbelebt\n" +
                "Der Eiskoloss kann für " + FROZEN_ROUNDS + " Runden nicht angreifen oder sich bewegen\n" +
                "Fällt der Eiskoloss kehrt Arn in eruem nächsten Zug zurück");
        desc.plus("Auf Seen +1 Würfel");
        desc.contra("Auf Wüsten -1 Zahl");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (area.getType() == AreaType.DESERT) {
            power.changeNumberPlus(-1);
        } else if (area.getType() == AreaType.LAKE) {
            power.changeDiceNumber(1);
        }
    }

    @Override
    public void turnStarted(ActionHandlers actionHandlers) {
        spawns.forEach(unit -> UnitControl.spawn(game, unit, unit.getLocation()));
        spawns.clear();
    }


    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Wassermagier");
        soldier.setIcon(46);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner) {
            @Override
            public void onDeath(BattleResult throughBattle) {
                BattleObject summon = new IceSummon();
                summon.setOwner(owner);
                summon.setLocation(getLocation());
                spawns.add(summon);
            }
        };
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Frostbeschwörer Arn");
        commander.setIcon(116);
        return commander;
    }

    private class IceSummon extends Monster {

        public IceSummon() {
            setIcon(117);
            setName("Eiskoloss");
            setStats(2, 4);
            IntBuff debuff = new IntBuff(BuffType.SET, 0);
            getMovesPerTurn().addBuff(debuff);
            getAttacksPerTurn().addBuff(debuff);
            WaterFraction.this.game.getBuffProcessor().timeoutBuff(debuff, FROZEN_ROUNDS);
        }

        @Override
        public void onDeath(BattleResult throughBattle) {
            BattleObject commander = createCommander();
            commander.setLocation(getLocation());
            spawns.add(commander);
        }
    }

}
