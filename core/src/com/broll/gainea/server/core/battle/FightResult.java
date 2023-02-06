package com.broll.gainea.server.core.battle;

import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.server.core.objects.BattleObject;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
public class FightResult {

    private List<Integer> attackRolls;
    private List<Integer> defenderRolls;
    private List<BattleObject> deadAttackers;
    private List<BattleObject> deadDefenders;
    private List<AttackDamage> damage = new ArrayList<>();

    public FightResult(List<Integer> attackRolls, List<Integer> defenderRolls) {
        this.attackRolls = attackRolls;
        this.defenderRolls = defenderRolls;
    }

    public void damage(BattleObject source, BattleObject target, boolean lethal){
        AttackDamage damage = new AttackDamage();
        damage.source = source;
        damage.target = target;
        damage.lethalHit = lethal;
        this.damage.add(damage);
    }

    public void killed(List<BattleObject> deadAttackers, List<BattleObject> deadDefenders){
        this.deadAttackers = deadAttackers;
        this.deadDefenders = deadDefenders;
    }

    public List<AttackDamage> getDamage() {
        return damage;
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

    public class AttackDamage{
        public BattleObject source;
        public BattleObject target;
        public boolean lethalHit;

        public NT_Battle_Damage nt(){
            NT_Battle_Damage nt = new NT_Battle_Damage();
            nt.source = source.getId();
            nt.target = target.getId();
            nt.lethal = lethalHit;
            return nt;
        }
    }
}
