package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BattleContext {

    protected Location location;
    protected Location sourceLocation;
    protected List<Unit> attackers;
    protected List<Unit> defenders;
    protected Player attackingPlayer;
    protected List<Player> defendingPlayers;

    public BattleContext(List<Unit> attackers, List<Unit> defenders) {
        this.location = defenders.get(0).getLocation();
        this.sourceLocation = attackers.get(0).getLocation();
        this.attackers = attackers;
        this.defenders = defenders;
        this.attackingPlayer = PlayerUtils.getOwner(attackers);
        this.defendingPlayers = defenders.stream().map(Unit::getOwner).distinct().collect(Collectors.toList());
    }

    public Location getLocation() {
        return location;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public Location getLocation(Player player) {
        if (isAttacker(player)) {
            return sourceLocation;
        } else if (isDefender(player)) {
            return location;
        }
        return null;
    }
    
    public List<Unit> getAttackers() {
        return attackers;
    }

    public List<Unit> getDefenders() {
        return defenders;
    }

    public List<Unit> getAliveAttackers() {
        return attackers.stream().filter(Unit::isAlive).collect(Collectors.toList());
    }

    public List<Unit> getAliveDefenders() {
        return defenders.stream().filter(Unit::isAlive).collect(Collectors.toList());
    }

    public List<Unit> getKilledAttackers() {
        return attackers.stream().filter(Unit::isDead).collect(Collectors.toList());
    }

    public List<Unit> getKilledDefenders() {
        return defenders.stream().filter(Unit::isDead).collect(Collectors.toList());
    }

    public boolean hasSurvivingAttackers() {
        return !getAliveAttackers().isEmpty();
    }

    public boolean hasSurvivingDefenders() {
        return !getAliveDefenders().isEmpty();
    }

    public List<Unit> getFightingArmy(Unit unit) {
        if (isAttacking(unit)) {
            return getAliveAttackers();
        } else if (isDefending(unit)) {
            return getAliveDefenders();
        }
        return new ArrayList<>();
    }

    public List<Unit> getUnits(Player player) {
        if (isAttacker(player)) {
            return attackers.stream().filter(it -> it.getOwner() == player).collect(Collectors.toList());
        } else if (isDefender(player)) {
            return defenders.stream().filter(it -> it.getOwner() == player).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<Unit> getOpposingUnits(Player player) {
        if (isAttacker(player)) {
            return getDefenders();
        } else if (isDefender(player)) {
            return getAttackers();
        }
        return new ArrayList<>();
    }

    public List<Unit> getOpposingFightingArmy(Unit unit) {
        if (isAttacking(unit)) {
            return getAliveDefenders();
        } else if (isDefending(unit)) {
            return getAliveAttackers();
        }
        return new ArrayList<>();
    }

    public boolean isParticipating(Player player) {
        return isAttacker(player) || isDefender(player);
    }

    public boolean isAttacker(Player player) {
        return this.attackingPlayer == player;
    }

    public boolean isAttacking(Unit unit) {
        return this.attackers.contains(unit);
    }

    public boolean isDefending(Unit unit) {
        return this.defenders.contains(unit);
    }

    public boolean isDefender(Player player) {
        return this.defendingPlayers.contains(player);
    }

    public boolean isNeutralAttacker() {
        return this.attackingPlayer == null;
    }

    public boolean isNeutralDefender() {
        return isNeutralOwner(this.defendingPlayers);
    }

    public boolean isNeutralParticipant() {
        return isNeutralAttacker() || isNeutralDefender();
    }

    public Player getAttackingPlayer() {
        return attackingPlayer;
    }

    protected boolean isNeutralOwner(List<Player> players) {
        return players.size() == 1 && players.get(0) == null;
    }

    protected List<Player> getNonNeutralOwners(List<Player> owners) {
        return owners.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Player> getDefendingPlayers() {
        return getNonNeutralOwners(this.defendingPlayers);
    }
}
