package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.battle.Battle;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightResult;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.BuffableInt;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BattleSimulation {
    private final static Logger Log = LoggerFactory.getLogger(BattleSimulation.class);
    private static final int SIMULATIONS = 10;

    public static List<Unit> calculateRequiredFighters(Location location, List<Unit> units, float winChance) {
        List<Unit> fighters = new ArrayList<>();
        for (Unit unit : units) {
            fighters.add(unit);
            float calculatedWinchance = calculateWinChance(location, fighters);
            if (calculatedWinchance >= winChance) {
                Log.trace("Attacking with " + fighters.size() + "/" + units.size() + " units should win with " + calculatedWinchance * 100 + "%");
                return fighters;
            }
        }
        return null;
    }

    public static float calculateCurrentWinChance(List<Unit> attackers, List<Unit> defenders) {
        float wins = 0;
        for (int i = 0; i < SIMULATIONS; i++) {
            if (winsBattle(attackers, defenders)) {
                wins++;
            }
        }
        float winChance = wins / (float) SIMULATIONS;
        Log.trace("Continuing attack should win with " + winChance * 100 + "%");
        return winChance;
    }

    public static float calculateWinChance(Location location, List<Unit> units) {
        float wins = 0;
        for (int i = 0; i < SIMULATIONS; i++) {
            if (winsBattle(location, units)) {
                wins++;
            }
        }
        return wins / (float) SIMULATIONS;
    }

    private static boolean winsBattle(Location location, List<Unit> units) {
        Player owner = PlayerUtils.getOwner(units);
        return winsBattle(units, PlayerUtils.getHostileArmy(owner, location));
    }

    public static boolean winsBattle(List<Unit> attackers, List<Unit> defenders) {
        UnitSimulationWrapper simulationWrapper = new UnitSimulationWrapper(attackers, defenders);
        while (attackers.stream().anyMatch(Unit::isAlive) && defenders.stream().anyMatch(Unit::isAlive)) {
            BattleContext context = new BattleContext(attackers, defenders);
            new Battle(context,
                    attackers.stream().filter(Unit::isAlive).collect(Collectors.toList()),
                    defenders.stream().filter(Unit::isAlive).collect(Collectors.toList())) {
                @Override
                protected void damage(FightResult result, Unit source, Unit target) {
                    target.takeDamage();
                }
            }.fight();
        }
        boolean winner = attackers.stream().anyMatch(Unit::isAlive);
        simulationWrapper.restore();
        return winner;
    }

    private static class UnitSimulationWrapper {

        private List<Unit> units;
        private List<BuffableInt<MapObject>> originalHealth;

        public UnitSimulationWrapper(List<Unit> attackers, List<Unit> defenders) {
            units = Stream.concat(attackers.stream(), defenders.stream()).collect(Collectors.toList());
            originalHealth = units.stream().map(Unit::getHealth).collect(Collectors.toList());
            units.forEach(it -> it.overwriteHealth(it.getHealth().copy()));
        }

        public void restore() {
            for (int i = 0; i < units.size(); i++) {
                units.get(i).overwriteHealth(originalHealth.get(i));
            }
        }

    }

}
