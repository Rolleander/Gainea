package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BattleResult {

    private List<BattleObject> attackers;
    private List<BattleObject> defenders;
    private Location location;

    public BattleResult(List<BattleObject> attackers, List<BattleObject> defenders, Location location) {
        this.attackers = attackers;
        this.defenders = defenders;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public Player getAttacker() {
        return attackers.get(0).getOwner();
    }

    public Player getDefender() {
        return defenders.get(0).getOwner();
    }

    public boolean attackersWon() {
        return defenders.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd) && attackers.stream().map(BattleObject::isAlive).reduce(false, Boolean::logicalOr);
    }

    public boolean defendersWon() {
        return attackers.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd) && defenders.stream().map(BattleObject::isAlive).reduce(false, Boolean::logicalOr);
    }

    public Player getWinnerPlayer() {
        if (attackersWon()) {
            return getAttacker();
        } else if (defendersWon()) {
            return getDefender();
        }
        return null;
    }

    public Player getLoserPlayer() {
        if (attackersWon()) {
            return getDefender();
        } else if (defendersWon()) {
            return getAttacker();
        }
        return null;
    }

    public boolean drawOrRetreat() {
        return !attackersWon() && !defendersWon();
    }

    public List<BattleObject> getAttackers() {
        return attackers;
    }

    public List<BattleObject> getDefenders() {
        return defenders;
    }

    public List<BattleObject> getWinnerUnits() {
        if (attackersWon()) {
            return getAttackers();
        } else if (defendersWon()) {
            return getDefenders();
        }
        return new ArrayList<>();
    }

    public List<BattleObject> getLoserUnits() {
        if (attackersWon()) {
            return getDefenders();
        } else if (defendersWon()) {
            return getAttackers();
        }
        return new ArrayList<>();
    }

}
