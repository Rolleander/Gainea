package com.broll.gainea.server.core.bot.strategy;

import com.broll.gainea.server.core.bot.impl.BotMove;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.google.common.collect.Lists;

import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LocationDanger {

    public static int getFleeToScore(Player owner, Location location) {
        int ownerPower = PlayerUtils.getUnits(owner, location).stream().mapToInt(Unit::getBattleStrength).sum();
        int surroundingEnemyPower = location.getConnectedLocations().stream().flatMap(it -> it.getInhabitants().stream())
                .filter(it -> it instanceof Unit && it.canMoveTo(location) && it.getOwner() != owner).filter(it -> {
                    if (it instanceof Monster) {
                        return ((Monster) it).mightAttackSoon();
                    }
                    return true;
                }).mapToInt(it -> ((Unit) it).getBattleStrength()).sum();
        int diff = Math.min(0, surroundingEnemyPower - ownerPower);
        return BotMove.MOVE_SCORE - diff;
    }

    public static double getAnnihilationChance(Player owner, Location location) {
        return location.getConnectedLocations().stream().mapToDouble(neighbour -> {
            MultiValueMap<Player, Unit> units = getEnemyUnits(owner, neighbour);
            return units.keySet().stream().mapToDouble(enemy ->
                    getAnnihilationChance(location, owner, enemy, units.getCollection(enemy))).max().orElse(0);
        }).max().orElse(0);
    }

    private static double getAnnihilationChance(Location to, Player owner, Player enemy, Collection<Unit> units) {
        List<Unit> defenders = PlayerUtils.getUnits(owner, to);
        List<Unit> attackers = new ArrayList<>(units).stream().filter(it -> it.canMoveTo(to)).collect(Collectors.toList());
        if (attackers.isEmpty()) {
            return 0;
        }
        if (enemy == null) {
            return attackers.stream().filter(it -> it instanceof Monster && ((Monster) it).mightAttackSoon())
                    .mapToDouble(it -> BattleSimulation.calculateCurrentWinChance(Lists.newArrayList(it), defenders)).max().orElse(0);
        } else {
            return BattleSimulation.calculateCurrentWinChance(attackers, defenders);
        }
    }

    private static MultiValueMap<Player, Unit> getEnemyUnits(Player owner, Location location) {
        MultiValueMap<Player, Unit> units = new MultiValueMap<>();
        location.getInhabitants().stream().filter(it -> it instanceof Unit).map(it -> (Unit) it)
                .filter(it -> it.getOwner() != owner).forEach(unit -> units.put(unit.getOwner(), unit));
        return units;
    }

}
