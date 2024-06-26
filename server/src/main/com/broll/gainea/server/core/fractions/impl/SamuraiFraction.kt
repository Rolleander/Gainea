package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.isAreaType

class SamuraiFraction : Fraction(FractionType.SAMURAI) {

    override val description: FractionDescription = FractionDescription(
        "",
        soldier = UnitDescription(name = "Samurai", icon = 111),
        commander = UnitDescription(name = "Ronin", icon = 113, power = 3, health = 3),
    ).apply {
        plus("Als Angreifer +1 Zahl")
        plus("Auf Bergen +1 Zahl")
        contra("Als Verteidiger höchste Würfelzahl 5")
    }


    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        if (context.isAttacker(owner)) {
            power.changeNumberPlus(1)
        } else {
            power.withHighestNumber(5)
        }
        if (context.location.isAreaType(AreaType.MOUNTAIN)) {
            power.changeNumberPlus(1)
        }
        return power
    }

}
