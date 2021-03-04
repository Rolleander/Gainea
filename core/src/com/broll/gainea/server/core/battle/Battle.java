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
        if (attackingPlayer == null) {
            this.attackPower = initWildAnimalPower(attackingUnits);
        } else {
            this.attackPower = attackingPlayer.getFraction().calcPower(location, attackingUnits, defendingUnits, true);
        }
        if (defendingPlayer == null) {
            this.defendingPower = initWildAnimalPower(defendingUnits);
        } else {
            this.defendingPower = defendingPlayer.getFraction().calcPower(location, defendingUnits, attackingUnits, false);
        }
    }

    private FightingPower initWildAnimalPower(List<BattleObject> units){
        FightingPower power = new FightingPower();
        power.setDiceCount(units.stream().map(it->it.getPower().getValue()).reduce(0,Integer::sum));
        return power;
    }

    public FightResult fight() {
        int attacks = attackPower.getDiceCount();
        int blocks = defendingPower.getDiceCount();
        int rawAttackerPower = attackers.stream().map(it->it.getPower().getValue()).reduce(0, Integer::sum);
        int rawDefenderPower = defenders.stream().map(it->it.getPower().getValue()).reduce(0, Integer::sum);
        int deltaAttacks = attacks - blocks;
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
        if (deltaAttacks < 0) {
            //less attacker dices => deal remaining damage to attacking units without dices
            blockWins += Math.min(deltaAttacks * -1, rawAttackerPower - attacks);
        } else if (deltaAttacks > 0) {
            //less defender dices => deal remaining damage to defending units without dices
            attackWins += Math.min(deltaAttacks, rawDefenderPower - blocks);
        }
        //sort ascending (so that weakest power level units die first)
        attackers.sort((o1, o2) -> Integer.compare(o1.getPower().getValue(), o2.getPower().getValue()));
        defenders.sort((o1, o2) -> Integer.compare(o1.getPower().getValue(), o2.getPower().getValue()));
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
        return new FightResult(allAttackRolls, allBlockRolls, deadAttackers, deadDefenders);
    }

    private boolean bothTeamsAlive() {
        return !allDead(attackers) && !allDead(defenders);
    }

    private boolean allDead(List<BattleObject> fighters) {
        return fighters.stream().map(BattleObject::isDead).reduce(true, Boolean::logicalAnd);
    }

    private int dealDamage(List<BattleObject> targets) {
        for (int i = 0; i < targets.size(); i++) {
            BattleObject fighter = targets.get(i);
            if (!fighter.isDead()) {
                fighter.takeDamage();
                return i;
            }
        }
        return -1;
    }
}
