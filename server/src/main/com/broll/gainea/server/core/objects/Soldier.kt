package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.player.Player

open class Soldier(owner: Player?) : Unit(owner) {
    var fraction: Fraction? = null
    var isCommander = false

    init {
        if (owner != null) {
            fraction = owner.fraction
        }
    }

    override fun calcFightingPower(context: BattleContext?): FightingPower? {
        return if (fraction == null) {
            super.calcFightingPower(context)
        } else fraction.calcFightingPower(this, context)
    }
}
