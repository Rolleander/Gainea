package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.recruit

class PoacherFraction : Fraction(FractionType.POACHER) {
    override fun description(): FractionDescription {
        val desc = FractionDescription(
                "",
                soldier = UnitDescription(name = "Wilderer", icon = 42),
                commander = UnitDescription(name = "Monsterzähmer", icon = 44, power = 1, health = 5),
        )
        desc.plus("Besiegte Monster werden rekrutiert")
        desc.contra("Gegen menschliche Truppen -1 Zahl für eigene Soldaten")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        if (context.getOpposingFightingArmy(soldier).none { it is Monster }) {
            power.changeNumberPlus(-1)
        }
        return power
    }

    override fun killedMonster(monster: Monster) {
        super.killedMonster(monster)
        game.recruit(owner, listOf(monster))
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Wilderer"
        soldier.icon = 42
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.name = "Monsterzähmer"
        commander.icon = 44
        commander.setStats(1, 5)
        return commander
    }
}
