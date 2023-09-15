package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class ShadowFraction extends Fraction {
    private static float SUMMON_CHANCE = 0.4f;

    public ShadowFraction() {
        super(FractionType.SHADOW);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Bei Kämpfen können gefallene Feinde zu Skeletten (1/1) werden");
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Steppen");
        desc.contra("Skelette haben -1 Zahl");
        return desc;
    }

    @Override
    public void killedMonster(Monster monster) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.PLAINS)) {
            return;
        }
        super.killedMonster(monster);
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
    public Soldier createCommander() {
        Soldier commander = new Soldier(owner);
        commander.setCommander(true);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Erznekromant Bal");
        commander.setIcon(21);
        return commander;
    }

    private void summonSkeletons(List<Unit> killedEnemies, Location location) {
        killedEnemies.forEach(it -> {
            if (RandomUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location);
            }
        });
    }

    private void summon(Location location) {
        Soldier skeleton = new Soldier(owner) {
            @Override
            public FightingPower calcFightingPower(BattleContext context) {
                return super.calcFightingPower(context).changeNumberPlus(-1);
            }
        };
        skeleton.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        skeleton.setIcon(94);
        skeleton.setName("Skelett");
        UnitControl.spawn(game, skeleton, location);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.isParticipating(owner)) {
            Location spawnLocation = result.getEndLocation(owner);
            List<Unit> killedEnemies = result.getOpposingUnits(owner).stream().filter(Unit::isDead).collect(Collectors.toList());
            summonSkeletons(killedEnemies, spawnLocation);
        }
    }

}
