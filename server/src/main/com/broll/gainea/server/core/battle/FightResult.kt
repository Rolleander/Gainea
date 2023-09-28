package com.broll.gainea.server.core.battle

import com.broll.gainea.net.NT_Battle_Damage
import com.broll.gainea.server.core.objects.Unit

class FightResult(val attackRolls: List<Int>, val defenderRolls: List<Int>) {
    var deadAttackers = listOf<Unit>()
    var deadDefenders = listOf<Unit>()

    private val damage = mutableListOf<AttackDamage>()
    fun damage(source: Unit, target: Unit, lethal: Boolean) {
        this.damage.add(AttackDamage(source, target, lethal))
    }

    fun killed(deadAttackers: List<Unit>, deadDefenders: List<Unit>) {
        this.deadAttackers = deadAttackers
        this.deadDefenders = deadDefenders
    }

    fun getDamage(): List<AttackDamage> {
        return damage
    }

    data class AttackDamage(
            val source: Unit,
            val target: Unit,
            val lethalHit: Boolean
    ) {
        fun nt(): NT_Battle_Damage {
            val nt = NT_Battle_Damage()
            nt.source = source.id
            nt.target = target.id
            nt.lethal = lethalHit
            return nt
        }
    }
}
