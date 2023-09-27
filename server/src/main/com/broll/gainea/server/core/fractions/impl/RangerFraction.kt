package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.LocationUtils

class RangerFraction : Fraction(FractionType.RANGER) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Gegen Monster +1 Zahl")
        desc.contra("Auf Schnee und Wüste -1 Zahl")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext?): FightingPower? {
        val power = super.calcFightingPower(soldier, context)
        if (context!!.getOpposingFightingArmy(soldier).stream().anyMatch { it: Unit? -> it is Monster }) {
            power!!.changeNumberPlus(1)
        }
        if (LocationUtils.isAreaType(context.location, AreaType.SNOW, AreaType.DESERT)) {
            power!!.changeNumberPlus(-1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Waldläuferin"
        soldier.setType(NT_Unit.TYPE_FEMALE.toInt())
        soldier.icon = 114
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Elfenschützin"
        commander.setType(NT_Unit.TYPE_FEMALE.toInt())
        commander.icon = 115
        return commander
    }
}
