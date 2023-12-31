package com.broll.gainea.server.core.fractions.impl

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
    override fun description(): FractionDescription {
        val desc = FractionDescription(
            "",
            soldier = UnitDescription(name = "Gardistenwache", icon = 19),
            commander = UnitDescription(name = "Elitegardist", icon = 15, power = 3, health = 3),
        )
        desc.plus("Als Verteidiger ist die niedrigste Würfelzahl 3")
        desc.plus("Zahl +1 für Verteidiger, die ihr Feld mindestens eine Runde nicht verlassen haben")
        desc.contra("Als Angreifer Zahl -1")
        return desc
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

    override fun createSoldier(): Soldier {
        val soldier: Soldier = GuardSoldier(owner)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Gardistenwache"
        soldier.icon = 19
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = GuardSoldier(owner)
        commander.commander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Elitegardist"
        commander.icon = 15
        return commander
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
