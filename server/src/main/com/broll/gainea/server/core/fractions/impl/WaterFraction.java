package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.List;

public class WaterFraction extends Fraction {

    private final static int FROZEN_ROUNDS = 2;

    private List<Unit> spawns = new ArrayList<>();

    public WaterFraction() {
        super(FractionType.WATER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Fällt Arn wird er in eurem nächsten Zug als Eiskoloss (2/4) wiederbelebt\n" +
                "Der Eiskoloss kann für " + FROZEN_ROUNDS + " Runden nicht angreifen oder sich bewegen\n" +
                "Fällt der Eiskoloss kehrt Arn in eruem nächsten Zug zurück");
        desc.plus("Einheiten auf Seen können eine weitere Aktion durchführen");
        desc.contra("Auf Wüsten und Bergen -1 Zahl");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (area.getType() == AreaType.DESERT || area.getType() == AreaType.MOUNTAIN) {
            power.changeNumberPlus(-1);
        }
    }

    @Override
    public void turnStarted(ActionHandlers actionHandlers) {
        spawns.forEach(unit -> UnitControl.spawn(game, unit, unit.getLocation()));
        spawns.clear();
    }


    private void movedUnit(Unit unit) {
        if (LocationUtils.isAreaType(unit.getLocation(), AreaType.LAKE)) {
            unit.turnStart();
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner) {
            @Override
            public void moved() {
                super.moved();
                movedUnit(this);
            }
        };
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
                Unit summon = new IceSummon();
                summon.setOwner(owner);
                summon.setLocation(getLocation());
                spawns.add(summon);
            }

            @Override
            public void moved() {
                super.moved();
                movedUnit(this);
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
        public FightingPower calcFightingPower(BattleContext context) {
            FightingPower power = super.calcFightingPower(context);
            if (context.getLocation() instanceof Area) {
                powerMutatorArea(power, (Area) context.getLocation());
            }
            return power;
        }

        @Override
        public void moved() {
            super.moved();
            movedUnit(this);
        }

        @Override
        public void onDeath(BattleResult throughBattle) {
            Unit commander = createCommander();
            commander.setLocation(getLocation());
            spawns.add(commander);
        }
    }

}
