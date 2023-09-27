package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.objects.Soldier

class MercenaryFraction : Fraction(FractionType.MERCENARY) {
    private var turns = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Jeden zweiten Zug erhaltet Ihr einen weiteren Soldat")
        desc.plus("Minimale Zahl beim Würfeln ist 2")
        desc.contra("Maximale Zahl beim Würfeln ist 5")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext?): FightingPower? {
        return super.calcFightingPower(soldier, context)!!.withHighestNumber(5).withLowestNumber(2)
    }

    override fun prepareTurn(actionHandlers: ActionHandlers?) {
        super.prepareTurn(actionHandlers)
        turns++
        if (turns >= SPAWN_TURN) {
            //spawn another soldier
            super.prepareTurn(actionHandlers)
            turns = 0
        }
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Söldner"
        soldier.icon = ICONS[RandomUtils.random(ICONS.size - 1)]
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Söldnerkommandant"
        commander.icon = 5
        return commander
    }

    companion object {
        private val ICONS = intArrayOf(2, 4, 8, 9, 13, 14, 17, 20, 26, 27, 29, 30, 31, 32, 34, 36, 37, 38, 39, 40, 41, 43)
        private const val SPAWN_TURN = 2
    }
}
