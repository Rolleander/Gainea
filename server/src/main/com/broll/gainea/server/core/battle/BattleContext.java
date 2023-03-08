package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BattleContext {

    protected Location location;
    protected Location sourceLocation;
    protected List<BattleObject> attackers;
    protected List<BattleObject> defenders;
    protected Player attackingPlayer;
    protected List<Player> defendingPlayers;

    public BattleContext(List<BattleObject> attackers, List<BattleObject> defenders) {
        this.location = defenders.get(0).getLocation();
        this.sourceLocation = attackers.get(0).getLocation();
        this.attackers = attackers;
        this.defenders = defenders;
        this.attackingPlayer = PlayerUtils.getOwner(attackers);
        this.defendingPlayers = defenders.stream().map(BattleObject::getOwner).distinct().collect(Collectors.toList());
    }

    public Location getLocation() {
        return location;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public List<BattleObject> getAttackers() {
        return attackers;
    }

    public List<BattleObject> getDefenders() {
        return defenders;
    }

    public List<BattleObject> getAliveAttackers() {
        return attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
    }

    public List<BattleObject> getAliveDefenders() {
        return defenders.stream().filter(BattleObject::isAlive).collect(Collectors.toList());
    }

    public List<BattleObject> getKilledAttackers() {
        return attackers.stream().filter(BattleObject::isDead).collect(Collectors.toList());
    }

    public List<BattleObject> getKilledDefenders() {
        return defenders.stream().filter(BattleObject::isDead).collect(Collectors.toList());
    }

    public boolean hasSurvivingAttackers() {
        return !getAliveAttackers().isEmpty();
    }

    public boolean hasSurvivingDefenders() {
        return !getAliveDefenders().isEmpty();
    }

    public List<BattleObject> getArmy(BattleObject unit) {
        if (isAttacking(unit)) {
            return getAliveAttackers();
        } else if (isDefending(unit)) {
            return getAliveDefenders();
        }
        return null;
    }

    public List<BattleObject> getOpposingArmy(BattleObject unit) {
        if (isAttacking(unit)) {
            return getAliveDefenders();
        } else if (isDefending(unit)) {
            return getAliveAttackers();
        }
        return null;
    }

    public boolean isParticipating(Player player) {
        return isAttacker(player) || isDefender(player);
    }

    public boolean isAttacker(Player player) {
        return this.attackingPlayer == player;
    }

    public boolean isAttacking(BattleObject unit) {
        return this.attackers.contains(unit);
    }

    public boolean isDefending(BattleObject unit) {
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
