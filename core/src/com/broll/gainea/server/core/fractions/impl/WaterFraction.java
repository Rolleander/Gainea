package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.List;

public class WaterFraction extends Fraction {

    private final static int FROZEN_TURNS = 2;

    private List<BattleObject> spawns = new ArrayList<>();

    public WaterFraction() {
        super(FractionType.WATER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Fällt Arn wird er im nächsten Zug als Eiskoloss (2/4) wiederbelebt\n" +
                "Der Eiskoloss kann für " + FROZEN_TURNS + " Runden nicht angreifen oder sich bewegen\n" +
                "Fällt der Eiskolos kehrt Arn im nächsten Zug zurück");
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
    public void killed(BattleObject unit, BattleResult throughBattle) {
        if (unit.getOwner() == owner) {
            if (unit instanceof IceSummon) {
                BattleObject commander = createCommander();
                commander.setLocation(unit.getLocation());
                spawns.add(commander);
            } else if (unit instanceof Commander) {
                BattleObject summon = new IceSummon();
                summon.setLocation(unit.getLocation());
                spawns.add(summon);
            }
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Wassermagier");
        soldier.setIcon(46);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Frostbeschwörer Arn");
        commander.setIcon(116);
    }

    private class IceSummon extends Monster {

        public IceSummon() {
            setIcon(117);
            setName("Eiskoloss");
            setStats(2, 4);
            IntBuff debuff = new IntBuff(BuffType.SET, 0);
            getMovesPerTurn().addBuff(debuff);
            getAttacksPerTurn().addBuff(debuff);
            game.getBuffProcessor().timeoutBuff(debuff, FROZEN_TURNS);
        }
    }


}
