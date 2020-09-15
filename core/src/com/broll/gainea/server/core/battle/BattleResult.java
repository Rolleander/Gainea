package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.Optional;

public class BattleResult {

    private List<BattleObject> attackers;
    private List<BattleObject> defenders;

    public BattleResult(List<BattleObject> attackers, List<BattleObject> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
    }

    public Player getAttacker() {
        return attackers.get(0).getOwner();
    }

    public Player getDefender() {
        return defenders.get(0).getOwner();
    }

    public boolean isMonsterFight() {
        return getDefender() == null;
    }

    public boolean attackersWon() {
        return defenders.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd);
    }

    public boolean defendersWon() {
        return defenders.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd);
    }

    public boolean attackersRetreated() {
        return !attackersWon() && !defendersWon();
    }

    public List<BattleObject> getAttackers() {
        return attackers;
    }

    public List<BattleObject> getDefenders() {
        return defenders;
    }

}
