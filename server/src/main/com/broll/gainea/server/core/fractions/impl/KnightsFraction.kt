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

class KnightsFraction : Fraction(FractionType.KNIGHTS) {
    override fun description(): FractionDescription {
        val desc = FractionDescription(
            "",
            soldier = UnitDescription(name = "Kreuzritter", icon = 11),
            commander = UnitDescription(
                name = "Kreuzritterchampion",
                icon = 7,
                power = 3,
                health = 3
            ),
        )
        desc.plus("Im 1v1 +1 Zahl, als Angreifer +2 Zahl")
        desc.contra("Auf Sümpfen -1 Zahl")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val army = context.getFightingArmy(soldier)
        val opponents = context.getOpposingFightingArmy(soldier)
        if (army.size == 1 && opponents.size == 1) {
            //1v1  +2 Z (atk) / +1 Z (def)
            val plus = if (context.isAttacking(army.first())) 2 else 1
            power.changeNumberPlus(plus)
        }
        if (context.location.isAreaType(AreaType.BOG)) {
            power.changeNumberPlus(-1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Kreuzritter"
        soldier.icon = 11
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.commander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Kreuzritterchampion"
        commander.icon = 7
        return commander
    }
}
