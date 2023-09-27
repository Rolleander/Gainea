package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.LocationUtils

class SamuraiFraction : Fraction(FractionType.SAMURAI) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Als Angreifer +1 Zahl")
        desc.plus("Auf Bergen +1 Zahl")
        desc.contra("Als Verteidiger höchste Würfelzahl 5")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext?): FightingPower? {
        val power = super.calcFightingPower(soldier, context)
        if (context!!.isAttacker(owner)) {
            power!!.changeNumberPlus(1)
        } else {
            power!!.withHighestNumber(5)
        }
        if (LocationUtils.isAreaType(context.location, AreaType.MOUNTAIN)) {
            power.changeNumberPlus(1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Samurai"
        soldier.icon = 111
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Ronin"
        commander.icon = 113
        return commander
    }
}
