package com.broll.gainea.server.core.battle;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

import java.util.Collections;
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

    private FightingPower initWildAnimalPower(List<BattleObject> units) {
        FightingPower power = new FightingPower();
        power.setDiceCount(units.stream().map(it -> it.getPower().getValue()).reduce(0, Integer::sum));
        return power;
    }

    public FightResult fight() {
        int attacks = attackPower.getDiceCount();
        int blocks = defendingPower.getDiceCount();
        int rawAttackerPower = attackers.stream().map(it -> it.getPower().getValue()).reduce(0, Integer::sum);
        int rawDefenderPower = defenders.stream().map(it -> it.getPower().getValue()).reduce(0, Integer::sum);
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
        FightResult result = new FightResult(allAttackRolls, allBlockRolls);
        //deal damage
        List<DamageDealer> remainingAttackers = attackers.stream().map(DamageDealer::from).collect(Collectors.toList());
        List<DamageDealer> remainingDefenders = defenders.stream().map(DamageDealer::from).collect(Collectors.toList());
        do {
            if (attackWins > 0) {
                dealDamage(result, remainingAttackers, remainingDefenders);
                attackWins--;
            }
            if (blockWins > 0) {
                dealDamage(result, remainingDefenders, remainingAttackers);
                blockWins--;
            }
            remainingAttackers.removeIf(it -> it.unit.isDead());
            remainingDefenders.removeIf(it -> it.unit.isDead());
        } while (attackWins > 0 || blockWins > 0);
        List<BattleObject> deadAttackers = attackers.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        List<BattleObject> deadDefenders = defenders.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        result.killed(deadAttackers, deadDefenders);
        return result;
    }

    private void dealDamage(FightResult result, List<DamageDealer> sources, List<DamageDealer> targets) {
        //shuffe for damage (so that same powerlevel units get hit randomly)
        Collections.shuffle(targets);
        //sort ascending (so that weakest power level units die first)
        targets.stream().sorted((o1, o2) -> Integer.compare(o1.unit.getPower().getValue(), o2.unit.getPower().getValue()))
                .findFirst().ifPresent(target -> {
            boolean lethal = target.unit.takeDamage();
            DamageDealer source = findDamageDealer(sources);
            source.remainingAttacks--;
            if (source != null) {
                if (lethal) {
                    source.unit.addKill();
                }
                result.damage(source.unit, target.unit, lethal);
            }
        });
    }

    private DamageDealer findDamageDealer(List<DamageDealer> sources) {
        DamageDealer dealer = RandomUtils.pickRandom(sources.stream().filter(it -> it.remainingAttacks > 0).collect(Collectors.toList()));
        if (dealer == null) {
            dealer = RandomUtils.pickRandom(sources);
        }
        return dealer;
    }

    private static class DamageDealer {
        private BattleObject unit;
        private int remainingAttacks;

        private static DamageDealer from(BattleObject object) {
            DamageDealer ds = new DamageDealer();
            ds.unit = object;
            ds.remainingAttacks = object.getPower().getValue();
            return ds;
        }
    }
}
