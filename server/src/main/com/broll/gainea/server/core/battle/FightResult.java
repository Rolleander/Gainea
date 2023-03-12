package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.server.core.objects.Unit;

import java.util.ArrayList;
import java.util.List;

public class FightResult {

    private List<Integer> attackRolls;
    private List<Integer> defenderRolls;
    private List<Unit> deadAttackers;
    private List<Unit> deadDefenders;
    private List<AttackDamage> damage = new ArrayList<>();

    public FightResult(List<Integer> attackRolls, List<Integer> defenderRolls) {
        this.attackRolls = attackRolls;
        this.defenderRolls = defenderRolls;
    }

    public void damage(Unit source, Unit target, boolean lethal) {
        AttackDamage damage = new AttackDamage();
        damage.source = source;
        damage.target = target;
        damage.lethalHit = lethal;
        this.damage.add(damage);
    }

    public void killed(List<Unit> deadAttackers, List<Unit> deadDefenders) {
        this.deadAttackers = deadAttackers;
        this.deadDefenders = deadDefenders;
    }

    public List<AttackDamage> getDamage() {
        return damage;
    }

    public List<Unit> getDeadAttackers() {
        return deadAttackers;
    }

    public List<Unit> getDeadDefenders() {
        return deadDefenders;
    }

    public List<Integer> getAttackRolls() {
        return attackRolls;
    }

    public List<Integer> getDefenderRolls() {
        return defenderRolls;
    }

    public static class AttackDamage {
        public Unit source;
        public Unit target;
        public boolean lethalHit;

        public NT_Battle_Damage nt() {
            NT_Battle_Damage nt = new NT_Battle_Damage();
            nt.source = source.getId();
            nt.target = target.getId();
            nt.lethal = lethalHit;
            return nt;
        }
    }
}
