package com.broll.gainea.test.battle;

import com.broll.gainea.server.core.battle.Battle;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightResult;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffableInt;
import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BattleTest {

    @Test
    public void test() {
        fight(Lists.newArrayList(fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1), fighter(1, 1),
                fighter(1, 1)), Lists.newArrayList(fighter(8, 8)));
    }

    private BattleObject fighter(int power, int health) {
        BattleObject bo = new Soldier(null);
        bo.setStats(power, health);
        return bo;
    }

    private void fight(List<BattleObject> attackers, List<BattleObject> defenders) {
        List<Integer> attackerHealth = attackers.stream().map(BattleObject::getHealth).map(BuffableInt::getValue).collect(Collectors.toList());
        List<Integer> defenderHealth = defenders.stream().map(BattleObject::getHealth).map(BuffableInt::getValue).collect(Collectors.toList());
        System.out.println("Attackers: " + attackers.stream().map(it -> it.getPower().getValue() + "/" + it.getHealth().getValue()).collect(Collectors.toList()));
        System.out.println("Defenders: " + defenders.stream().map(it -> it.getPower().getValue() + "/" + it.getHealth().getValue()).collect(Collectors.toList()));
        FightResult result = battle(attackers, defenders);
        System.out.println("Attacker Rolls: " + result.getAttackRolls());
        System.out.println("Defender Rolls: " + result.getDefenderRolls());
        List<Integer> attackerHealth2 = attackers.stream().map(BattleObject::getHealth).map(BuffableInt::getValue).collect(Collectors.toList());
        List<Integer> defenderHealth2 = defenders.stream().map(BattleObject::getHealth).map(BuffableInt::getValue).collect(Collectors.toList());
        List<Integer> attackerDamage = new ArrayList<>();
        List<Integer> defenderDamage = new ArrayList<>();
        for (int i = 0; i < attackerHealth.size(); i++) {
            attackerDamage.add(attackerHealth.get(i) - attackerHealth2.get(i));
        }
        for (int i = 0; i < defenderHealth.size(); i++) {
            defenderDamage.add(defenderHealth.get(i) - defenderHealth2.get(i));
        }
        System.out.println("Damage to Attackers: " + attackerDamage);
        System.out.println("Damage to Defenders: " + defenderDamage);
        if (attackers.stream().map(BattleObject::isAlive).reduce(false, Boolean::logicalOr) && defenders.stream().map(BattleObject::isAlive).reduce(false, Boolean::logicalOr)) {
            fight(attackers.stream().filter(BattleObject::isAlive).collect(Collectors.toList()), defenders.stream().filter(BattleObject::isAlive).collect(Collectors.toList()));
        }
    }

    private FightResult battle(List<BattleObject> attackers, List<BattleObject> defenders) {
        Area area = new Area(GaineaMap.Areas.DUNKLESMEER);
        area.setType(AreaType.LAKE);
        BattleContext context = new BattleContext(area, attackers, defenders);
        Battle battle = new Battle(context, attackers, defenders);
        return battle.fight();
    }

}
