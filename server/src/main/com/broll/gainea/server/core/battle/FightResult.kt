package com.broll.gainea.server.core.battle

import com.broll.gainea.net.NT_Battle_Damage
import com.broll.gainea.server.core.objects.Unit

class FightResult(val attackRolls: List<Int?>, val defenderRolls: List<Int?>) {
    var deadAttackers: List<Unit?>? = null
        private set
    var deadDefenders: List<Unit?>? = null
        private set
    private val damage: MutableList<AttackDamage?> = ArrayList()
    fun damage(source: Unit?, target: Unit?, lethal: Boolean) {
        val damage = AttackDamage()
        damage.source = source
        damage.target = target
        damage.lethalHit = lethal
        this.damage.add(damage)
    }

    fun killed(deadAttackers: List<Unit?>?, deadDefenders: List<Unit?>?) {
        this.deadAttackers = deadAttackers
        this.deadDefenders = deadDefenders
    }

    fun getDamage(): List<AttackDamage?> {
        return damage
    }

    class AttackDamage {
        var source: Unit? = null
        var target: Unit? = null
        var lethalHit = false
        fun nt(): NT_Battle_Damage {
            val nt = NT_Battle_Damage()
            nt.source = source.getId()
            nt.target = target.getId()
            nt.lethal = lethalHit
            return nt
        }
    }
}
