package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.battle.Battle;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightResult;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BattleSimulation {

    private static final int SIMULATIONS = 10;

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

    public static float calculateCurrentWinChance(List<BattleObject> attackers, List<BattleObject> defenders) {
        float wins = 0;
        for (int i = 0; i < SIMULATIONS; i++) {
            if (winsBattle(attackers, defenders)) {
                wins++;
            }
        }
        return wins / (float) SIMULATIONS;
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
        return winsBattle(units, PlayerUtils.getHostileArmy(owner, location));
    }

    private static boolean winsBattle(List<BattleObject> attackingUnits, List<BattleObject> defendingUnits) {
        List<BattleObject> attackers = attackingUnits.stream().map(SimulationUnit::new).collect(Collectors.toList());
        List<BattleObject> defenders = defendingUnits.stream().map(SimulationUnit::new).collect(Collectors.toList());
        return isWinningBattle(attackers, defenders);
    }

    private static boolean isWinningBattle(List<BattleObject> attackers, List<BattleObject> defenders) {
        do {
            BattleContext context = new BattleContext(attackers, defenders);
            FightResult result = new Battle(context, attackers, defenders).fight();
            attackers.removeAll(result.getDeadAttackers());
            defenders.removeAll(result.getDeadDefenders());
        } while (!attackers.isEmpty() && !defenders.isEmpty());
        return !attackers.isEmpty();
    }

    private static class SimulationUnit extends BattleObject {

        public SimulationUnit(BattleObject original) {
            super(original.getOwner());
            copy(original, this);
        }
    }

}
