package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.player.Player

class GuardsFraction : Fraction(FractionType.GUARDS) {

    override val description: FractionDescription =
        FractionDescription(
            "",
            soldier = UnitDescription(name = "Gardistenwache", icon = 19),
            commander = UnitDescription(name = "Elitegardist", icon = 15, power = 3, health = 3),
        ).apply {
            plus("Als Verteidiger ist die niedrigste Würfelzahl 3")
            plus("Zahl +1 für Verteidiger, die ihr Feld mindestens eine Runde nicht verlassen haben")
            contra("Als Angreifer Zahl -1")
        }


    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        if (context.isAttacking(soldier)) {
            power.changeNumberPlus(-1)
        } else {
            power.withLowestNumber(3)
        }
        return power
    }

    override fun createCommander(): Soldier {
        val commander = GuardSoldier(owner)
        commander.commander = true
        commander.initFrom(description.commander)
        return commander
    }

    override fun createSoldier(): Soldier {
        val soldier = GuardSoldier(owner)
        soldier.initFrom(description.soldier)
        soldier.setType(NT_Unit.TYPE_FEMALE.toInt())
        return soldier
    }

    private inner class GuardSoldier(owner: Player) :
        Soldier(owner, fraction = this@GuardsFraction) {
        private val buff = IntBuff(BuffType.ADD, 1)
        private var lastLocation: Location? = null
        override fun prepareForTurnStart() {
            if (location == lastLocation) {
                numberPlus.addBuff(buff)
            } else {
                numberPlus.clearBuff(buff)
                lastLocation = location
            }
            super.prepareForTurnStart()
        }

        override fun calcFightingPower(context: BattleContext): FightingPower {
            if (context.isAttacking(this)) {
                numberPlus.clearBuff(buff)
            }
            return super.calcFightingPower(context)
        }
    }
}
