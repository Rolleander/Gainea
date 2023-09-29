package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils

class VikingFraction : Fraction(FractionType.VIKINGS) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Können Schiffe in jede Richtung gehen")
        desc.plus("Auf Schiffen Zahl +1")
        desc.plus("Auf Eis Zahl +1")
        desc.contra("Auf Wüste Zahl -2")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val location = context.location
        if (location is Ship || LocationUtils.isAreaType(location, AreaType.SNOW)) {
            power.changeNumberPlus(1)
        }
        if (LocationUtils.isAreaType(location, AreaType.DESERT)) {
            power.changeNumberPlus(-2)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier: Soldier = VikingSoldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Wikinger"
        soldier.icon = 106
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = VikingSoldier(owner)
        commander.isCommander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Jarl Olaf"
        commander.icon = 104
        return commander
    }

    private class VikingSoldier(owner: Player) : Soldier(owner) {
        override fun canMoveTo(to: Location): Boolean {
            return to.traversable
        }
    }
}
