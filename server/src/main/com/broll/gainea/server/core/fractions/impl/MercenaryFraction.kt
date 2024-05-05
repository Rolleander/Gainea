package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier

class MercenaryFraction : Fraction(FractionType.MERCENARY) {
    private var turns = 0
    override val description: FractionDescription = FractionDescription(
        "",
        soldier = UnitDescription(name = "Söldner", icon = 2),
        commander = UnitDescription(
            name = "Söldnerkommandant",
            icon = 5,
            power = 3,
            health = 3
        ),
    ).apply {
        plus("Jeden zweiten Zug erhaltet Ihr einen weiteren Soldat")
        contra("In Unterzahl höchste Würfelzahl 5")
    }


    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val army = context.getFightingArmy(soldier)
        val opponents = context.getOpposingFightingArmy(soldier)
        if (army.size < opponents.size) {
            power.withHighestNumber(5)
        }
        return power
    }


    override fun prepareTurn() {
        turns++
        if (turns >= SPAWN_TURN) {
            //spawn another soldier
            placeSoldier()
            turns = 0
        }
        super.prepareTurn()
    }

    override fun createSoldier(): Soldier = super.createSoldier().apply {
        icon = ICONS[RandomUtils.random(ICONS.size - 1)]
    }

    companion object {
        private val ICONS = intArrayOf(
            2,
            4,
            8,
            9,
            13,
            14,
            17,
            20,
            26,
            27,
            29,
            30,
            31,
            32,
            34,
            36,
            37,
            38,
            39,
            40,
            41,
            43
        )
        private const val SPAWN_TURN = 2
    }
}
