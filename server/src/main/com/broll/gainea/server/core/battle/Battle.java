package com.broll.gainea.server.core.battle;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.objects.Unit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Battle {

    private RollResult attackerRolls;
    private RollResult defenderRolls;

    private List<Unit> attackers;
    private List<Unit> defenders;

    public Battle(List<Unit> attackingUnits, List<Unit> defendingUnits, RollResult attackerRolls, RollResult defenderRolls) {
        this.attackers = attackingUnits;
        this.defenders = defendingUnits;
        this.attackerRolls = attackerRolls;
        this.defenderRolls = defenderRolls;
    }

    public Battle(BattleContext context, List<Unit> attackingUnits, List<Unit> defendingUnits) {
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
        List<Unit> deadAttackers = attackers.stream().filter(Unit::isDead).collect(Collectors.toList());
        List<Unit> deadDefenders = defenders.stream().filter(Unit::isDead).collect(Collectors.toList());
        result.killed(deadAttackers, deadDefenders);
        return result;
    }

    private void dealDamage(FightResult result, RollResult.Roll winningRoll, List<Unit> sourceUnits, List<Unit> targetUnits) {
        Unit source = winningRoll.source;
        if (source == null) {
            source = RandomUtils.pickRandom(sourceUnits);
        }
        Unit target = getDamageTarget(targetUnits);
        if (target != null) {
            damage(result, source, target);
        }
    }

    protected void damage(FightResult result, Unit source, Unit target) {
        boolean lethal = target.takeDamage();
        if (lethal) {
            source.addKill();
        }
        result.damage(source, target, lethal);
    }


    private Unit getDamageTarget(List<Unit> targetUnits) {
        List<Unit> targets = targetUnits.stream().filter(Unit::isAlive).collect(Collectors.toList());
        //shuffe for damage (so that same powerlevel units get hit randomly)
        Collections.shuffle(targets);
        //sort ascending (so that weakest power level units die first)
        return targets.stream().min(Comparator.comparingInt(o -> o.getPower().getValue())).orElse(null);
    }

}
