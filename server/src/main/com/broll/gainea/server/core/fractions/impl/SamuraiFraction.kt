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
    override fun description(): FractionDescription {
        val desc = FractionDescription(
                "",
                soldier = UnitDescription(name = "Samurai", icon = 111),
                commander = UnitDescription(name = "Ronin", icon = 113, power = 3, health = 3),
        )
        desc.plus("Als Angreifer +1 Zahl")
        desc.plus("Auf Bergen +1 Zahl")
        desc.contra("Als Verteidiger höchste Würfelzahl 5")
        return desc
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

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Samurai"
        soldier.icon = 111
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Ronin"
        commander.icon = 113
        return commander
    }
}
