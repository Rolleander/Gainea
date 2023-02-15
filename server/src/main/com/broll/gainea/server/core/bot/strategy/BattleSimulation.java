package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.battle.Battle;
import com.broll.gainea.server.core.battle.FightResult;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BattleSimulation {

    private static int SIMULATIONS = 10;

    public static List<BattleObject> calculateRequiredFighters(Location location, List<BattleObject> units, float winChance) {
        List<BattleObject> fighters = new ArrayList<>();
        for (BattleObject unit : units) {
            fighters.add(unit);
            if (calculateWinChance(location, fighters) >= winChance) {
                return fighters;
            }
        }
        return null;
    }

    public static float calculateWinChance(Location location, List<BattleObject> units) {
        float wins = 0;
        for (int i = 0; i < SIMULATIONS; i++) {
            if (winsBattle(location, units)) {
                wins++;
            }
        }
        return wins / (float) SIMULATIONS;
    }

    private static boolean winsBattle(Location location, List<BattleObject> units) {
        Player owner = PlayerUtils.getOwner(units);
        List<BattleObject> defenders = PlayerUtils.getHostileArmy(owner, location).stream().map(SimulationUnit::new).collect(Collectors.toList());
        do {
            Player defender = PlayerUtils.getOwner(defenders);
            List<BattleObject> defendingUnits = defenders.stream().filter(it -> it.getOwner() == defender).collect(Collectors.toList());
            defenders.removeAll(defendingUnits);
            List<BattleObject> attackingUnits = units.stream().map(SimulationUnit::new).collect(Collectors.toList());
            if (!winsBattle(location, attackingUnits, defendingUnits)) {
                return false;
            }
        } while (!defenders.isEmpty());
        return true;
    }

    private static boolean winsBattle(Location location, List<BattleObject> attackingUnits, List<BattleObject> defendingUnits) {
        do {
            FightResult result = new Battle(location, PlayerUtils.getOwner(attackingUnits), attackingUnits, PlayerUtils.getOwner(defendingUnits), defendingUnits).fight();
            attackingUnits.removeAll(result.getDeadAttackers());
            defendingUnits.removeAll(result.getDeadDefenders());
        } while (!defendingUnits.isEmpty() && !attackingUnits.isEmpty());
        return !attackingUnits.isEmpty();
    }

    private static class SimulationUnit extends BattleObject {

        public SimulationUnit(BattleObject original) {
            super(original.getOwner());
            copy(original, this);
        }
    }

}
