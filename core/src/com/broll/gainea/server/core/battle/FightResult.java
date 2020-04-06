package com.broll.gainea.server.core.battle;

import com.broll.gainea.server.core.objects.BattleObject;

import java.util.List;
public class FightResult {

    private List<Integer> attackRolls;
    private List<Integer> defenderRolls;
    private List<BattleObject> deadAttackers;
    private List<BattleObject> deadDefenders;

    public FightResult(List<Integer> attackRolls, List<Integer> defenderRolls, List<BattleObject> deadAttackers, List<BattleObject> deadDefenders) {
        this.attackRolls = attackRolls;
        this.defenderRolls = defenderRolls;
        this.deadAttackers = deadAttackers;
        this.deadDefenders = deadDefenders;
    }

    public List<BattleObject> getDeadAttackers() {
        return deadAttackers;
    }

    public List<BattleObject> getDeadDefenders() {
        return deadDefenders;
    }

    public List<Integer> getAttackRolls() {
        return attackRolls;
    }

    public List<Integer> getDefenderRolls() {
        return defenderRolls;
    }
}
