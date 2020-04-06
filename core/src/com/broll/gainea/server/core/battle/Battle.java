package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Battle {

    private FightingPower attackPower;
    private FightingPower defendingPower;

    private List<BattleObject> attackers;
    private List<BattleObject> defenders;

    public Battle(Location location, Player attackingPlayer, List<BattleObject> attackingUnits, Player defendingPlayer, List<BattleObject> defendingUnits) {
        this.attackers = attackingUnits;
        this.defenders = defendingUnits;
        this.attackPower = attackingPlayer.getFraction().calcPower(location, attackingUnits, defendingUnits, true);
        this.defendingPower = defendingPlayer.getFraction().calcPower(location, defendingUnits, attackingUnits, false);
    }

    public Battle(Location location, Player attackingPlayer, List<BattleObject> attackingUnits, List<BattleObject> monsters) {
        this.attackers = attackingUnits;
        this.defenders = monsters;
        this.attackPower = attackingPlayer.getFraction().calcPower(location, attackingUnits, this.defenders, true);
        this.defendingPower = new FightingPower();
        this.defendingPower.setDiceCount(monsters.stream().map(BattleObject::getPower).reduce(0, Integer::sum));
    }

    public FightResult fight() {
        int attacks = attackPower.getDiceCount();
        int blocks = defendingPower.getDiceCount();
        int battleSize = Math.min(attacks, blocks);
        List<Integer> allAttackRolls = attackPower.roll();
        List<Integer> allBlockRolls = defendingPower.roll();
        List<Integer> attackRolls = allAttackRolls.stream().limit(battleSize).collect(Collectors.toList());
        List<Integer> blockRolls = allBlockRolls.stream().limit(battleSize).collect(Collectors.toList());
        int attackWins = 0;
        for (int i = 0; i < battleSize; i++) {
            Integer attack = attackRolls.get(i);
            Integer block = blockRolls.get(i);
            if (attack > block) {
                attackWins++;
            }
        }
        int blockWins = battleSize - attackWins;
        //sort ascending (so that weakest power level units die first)
        attackers.sort((o1, o2) -> Integer.compare(o1.getPower(), o2.getPower()));
        defenders.sort((o1, o2) -> Integer.compare(o1.getPower(), o2.getPower()));
        //deal damage
        do {
            if (attackWins > 0) {
                dealDamage(defenders);
                attackWins--;
            }
            if (blockWins > 0) {
                dealDamage(attackers);
                blockWins--;
            }
        } while ((attackWins > 0 || blockWins > 0) && bothTeamsAlive());
        List<BattleObject> deadAttackers = attackers.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        List<BattleObject> deadDefenders = defenders.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        return new FightResult(attackRolls, blockRolls, deadAttackers, deadDefenders);
    }

    private boolean bothTeamsAlive() {
        return !allDead(attackers) && !allDead(defenders);
    }

    private boolean allDead(List<BattleObject> fighters) {
        return fighters.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd);
    }

    private void dealDamage(List<BattleObject> targets) {
        for (int i = 0; i < targets.size(); i++) {
            BattleObject fighter = targets.get(i);
            if (!fighter.isDead()) {
                fighter.takeDamage();
                return;
            }
        }
    }
}
