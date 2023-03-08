package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class ShadowFraction extends Fraction {
    private static float SUMMON_CHANCE = 0.35f;

    public ShadowFraction() {
        super(FractionType.SHADOW);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Bei siegreichen Kämpfen können gefallene Feinde zu Skeletten (1/1) werden");
        desc.contra("Auf Grasland -2 Würfel");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (area.getType() == AreaType.PLAINS) {
            power.changeDiceNumber(-2);
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Schatten");
        soldier.setIcon(12);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Erznekromant Bal");
        commander.setIcon(21);
        return commander;
    }

    private void afterAttack(List<BattleObject> killedEnemies, Location location) {
        killedEnemies.forEach(it -> {
            if (RandomUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location);
            }
        });
    }

    private void summon(Location location) {
        Soldier skeleton = createSoldier();
        skeleton.setIcon(94);
        skeleton.setName("Skelett");
        UnitControl.spawn(game, skeleton, location);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.isWinner(ShadowFraction.this.owner)) {
            afterAttack(result.getLoserDeadUnits(), result.getLocation());
        }
    }

}
