package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.LocationUtils

class KnightsFraction : Fraction(FractionType.KNIGHTS) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("In Unterzahl +1 Zahl")
        desc.plus("Im 1v1 +2 Zahl")
        desc.contra("Auf Sümpfen und Seen -1 Zahl")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val army = context.getFightingArmy(soldier)
        val opponents = context.getOpposingFightingArmy(soldier)
        if (army.size < opponents.size) {
            // smaller army +1 Z
            power.changeNumberPlus(1)
        } else if (army.size == 1 && opponents.size == 1) {
            //1v1  +2 Z
            power.changeNumberPlus(2)
        }
        if (LocationUtils.isAreaType(context.location, AreaType.BOG, AreaType.LAKE)) {
            power.changeNumberPlus(-1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Kreuzritter"
        soldier.icon = 11
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Kreuzritterchampion"
        commander.icon = 7
        return commander
    }
}
