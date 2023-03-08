package com.broll.gainea.server.core.battle;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Battle {

    private RollResult attackerRolls;
    private RollResult defenderRolls;

    private List<BattleObject> attackers;
    private List<BattleObject> defenders;

    public Battle(List<BattleObject> attackingUnits, List<BattleObject> defendingUnits, RollResult attackerRolls, RollResult defenderRolls) {
        this.attackers = attackingUnits;
        this.defenders = defendingUnits;
        this.attackerRolls = attackerRolls;
        this.defenderRolls = defenderRolls;
    }

    public Battle(BattleContext context, List<BattleObject> attackingUnits, List<BattleObject> defendingUnits) {
        this(attackingUnits, defendingUnits, new RollResult(context, attackingUnits),
                new RollResult(context, defendingUnits));
    }

    public FightResult fight() {
        int attacks = attackerRolls.count();
        int blocks = defenderRolls.count();
        int battleSize = Math.min(attacks, blocks);
        List<RollResult.Roll> allAttackRolls = attackerRolls.getRolls();
        List<RollResult.Roll> allBlockRolls = defenderRolls.getRolls();
        List<RollResult.Roll> attackRolls = allAttackRolls.stream().limit(battleSize).collect(Collectors.toList());
        List<RollResult.Roll> blockRolls = allBlockRolls.stream().limit(battleSize).collect(Collectors.toList());
        FightResult result = new FightResult(allAttackRolls.stream().map(it -> it.value).collect(Collectors.toList()),
                allBlockRolls.stream().map(it -> it.value).collect(Collectors.toList()));
        for (int i = 0; i < battleSize; i++) {
            RollResult.Roll attack = attackRolls.get(i);
            RollResult.Roll block = blockRolls.get(i);
            if (attack.value > block.value) {
                dealDamage(result, attack, attackers, defenders);
            } else {
                dealDamage(result, block, defenders, attackers);
            }
        }
        List<BattleObject> deadAttackers = attackers.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        List<BattleObject> deadDefenders = defenders.stream().filter(BattleObject::isDead).collect(Collectors.toList());
        result.killed(deadAttackers, deadDefenders);
        return result;
    }

    private void dealDamage(FightResult result, RollResult.Roll winningRoll, List<BattleObject> sourceUnits, List<BattleObject> targetUnits) {
        BattleObject source = winningRoll.source;
        if (source == null) {
            source = RandomUtils.pickRandom(sourceUnits);
        }
        BattleObject target = getDamageTarget(targetUnits);
        if (target != null) {
            boolean lethal = target.takeDamage();
            if (lethal) {
                source.addKill();
            }
            result.damage(source, target, lethal);
        }
    }

    private BattleObject getDamageTarget(List<BattleObject> targetUnits) {
        List<BattleObject> targets = new ArrayList<>(targetUnits);
        //shuffe for damage (so that same powerlevel units get hit randomly)
        Collections.shuffle(targets);
        //sort ascending (so that weakest power level units die first)
        return targets.stream().min(Comparator.comparingInt(o -> o.getPower().getValue())).orElse(null);
    }

}
