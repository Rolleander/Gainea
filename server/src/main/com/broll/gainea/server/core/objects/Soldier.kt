package com.broll.gainea.server.core.objects

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

open class Soldier(owner: Player,
                   var fraction: Fraction? = null,
                   var isCommander: Boolean = false) : Unit(owner) {

    init {
        if (owner.isNeutral()) {
            this.fraction = null
        }
    }

    override fun init(game: Game) {
        super.init(game)
        if (isCommander) {
            description = "Feldherr"
        }
    }

    override fun calcFightingPower(context: BattleContext): FightingPower {
        return if (fraction == null) {
            super.calcFightingPower(context)
        } else fraction!!.calcFightingPower(this, context)
    }
}
