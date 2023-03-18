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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BattleSimulation {

    private static final int SIMULATIONS = 10;

    public static List<Unit> calculateRequiredFighters(Location location, List<Unit> units, float winChance) {
        List<Unit> fighters = new ArrayList<>();
        for (Unit unit : units) {
            fighters.add(unit);
            if (calculateWinChance(location, fighters) >= winChance) {
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
        return wins / (float) SIMULATIONS;
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

    private static boolean winsBattle(List<Unit> attackers, List<Unit> defenders) {
        if (defenders.isEmpty() || attackers.isEmpty()) {
            return true;
        }
        UnitSimulationWrapper simulationWrapper = new UnitSimulationWrapper(attackers, defenders);
        do {
            BattleContext context = new BattleContext(attackers, defenders);
            FightResult result = new Battle(context, attackers, defenders).fight();
            attackers.removeAll(result.getDeadAttackers());
            defenders.removeAll(result.getDeadDefenders());
        } while (!attackers.isEmpty() && !defenders.isEmpty());
        simulationWrapper.restore();
        return !attackers.isEmpty();
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
