package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class BattleResult extends BattleContext {

    private boolean retreated;

    private boolean attackersWon;
    private boolean defendersWon;

    public BattleResult(boolean retreated, BattleContext context) {
        super(context.attackers, context.defenders);
        this.retreated = retreated;
        this.attackersWon = !retreated && hasSurvivingAttackers() && !hasSurvivingDefenders();
        this.defendersWon = retreated || (hasSurvivingDefenders() && !hasSurvivingAttackers());
    }

    public List<Player> getWinningPlayers() {
        return getNonNeutralOwners(getWinnerOwners());
    }

    public List<Player> getLosingPlayers() {
        return getNonNeutralOwners(getLoserOwners());
    }

    public boolean isWinner(Player player) {
        return getWinnerOwners().contains(player);
    }

    public boolean isLoser(Player player) {
        return getLoserOwners().contains(player);
    }

    public boolean isNeutralWinner() {
        return isNeutralOwner(getWinnerOwners());
    }

    public boolean isNeutralLoser() {
        return isNeutralOwner(getLoserOwners());
    }

    public Location getAttackerEndLocation() {
        return attackersWon() ? getLocation() : getSourceLocation();
    }

    public boolean attackersWon() {
        return attackersWon;
    }

    public boolean defendersWon() {
        return defendersWon;
    }

    private List<Player> getWinnerOwners() {
        if (attackersWon()) {
            return Lists.newArrayList(attackingPlayer);
        } else if (defendersWon()) {
            return defendingPlayers;
        }
        return new ArrayList<>();
    }

    private List<Player> getLoserOwners() {
        if (attackersWon()) {
            return defendingPlayers;
        } else if (defendersWon()) {
            return Lists.newArrayList(attackingPlayer);
        }
        return new ArrayList<>();
    }

    public boolean attackerRetreated() {
        return retreated;
    }

    public boolean isDraw() {
        return !attackersWon() && !defendersWon();
    }

    public List<Unit> getWinnerUnits() {
        if (attackersWon()) {
            return getAttackers();
        } else if (defendersWon()) {
            return getDefenders();
        }
        return new ArrayList<>();
    }

    public List<Unit> getLoserUnits() {
        if (attackersWon()) {
            return getDefenders();
        } else if (defendersWon()) {
            return getAttackers();
        }
        return new ArrayList<>();
    }

    public List<Unit> getWinnerSurvivingUnits() {
        if (attackersWon()) {
            return getAliveAttackers();
        } else if (defendersWon()) {
            return getAliveDefenders();
        }
        return new ArrayList<>();
    }

    public List<Unit> getLoserSurvivingUnits() {
        if (attackersWon()) {
            return getAliveDefenders();
        } else if (defendersWon()) {
            return getAliveAttackers();
        }
        return new ArrayList<>();
    }

    public List<Unit> getWinnerDeadUnits() {
        if (attackersWon()) {
            return getKilledAttackers();
        } else if (defendersWon()) {
            return getKilledDefenders();
        }
        return new ArrayList<>();
    }

    public List<Unit> getLoserDeadUnits() {
        if (attackersWon()) {
            return getKilledDefenders();
        } else if (defendersWon()) {
            return getKilledAttackers();
        }
        return new ArrayList<>();
    }

    public Location getEndLocation(Player player) {
        if (isAttacker(player)) {
            return getAttackerEndLocation();
        } else if (isDefender(player)) {
            return location;
        }
        return null;
    }

    public List<Player> getKillingPlayers(Unit unit) {
        if (isAttacking(unit)) {
            return getDefendingPlayers();
        } else if (isDefending(unit)) {
            return Lists.newArrayList(getAttackingPlayer());
        }
        return new ArrayList<>();
    }
}
