package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getCommander
import com.broll.gainea.server.core.utils.isCommander

class BarbarianFraction : Fraction(FractionType.BARBARIANS) {
    private var turns = 0
    private var brother: BarbarianBrother? = null
    override val description: FractionDescription =
        FractionDescription(
            "",
            soldier = UnitDescription(name = "Barbarenrkieger", icon = 103),
            commander = UnitDescription(
                name = "Barbarenanführer",
                icon = 45,
                power = 3,
                health = 3
            ),
        ).apply {
            plus("Nach " + SUMMON_TURN + " Runden ruft der Kommandant seine zweite Hand (2/3) herbei")
            plus("+1 Zahl, wenn Kommandant und zweite Hand zusammen kämpfen")
            contra("-1 Zahl, wenn keine Barbarenrkieger im Kampf beteiligt sind")
        }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val army = context.getFightingArmy(soldier)
        if (army.none { isPlainBarbarianSoldier(it) }) {
            power.changeNumberPlus(-1)
        }
        if (army.any { it.isCommander() }
            && army.any { it is BarbarianBrother }) {
            power.changeNumberPlus(1)
        }
        return power
    }

    private fun isPlainBarbarianSoldier(unit: Unit): Boolean {
        return unit is Soldier && !unit.commander &&
                unit !is BarbarianBrother && unit.fraction === this
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.setStats(description.soldier.power, description.soldier.health)
        soldier.name = description.soldier.name
        soldier.icon = description.soldier.icon
        soldier.description = description.soldier.description
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.commander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Barbarenanführer"
        commander.icon = 45
        return commander
    }

    override fun prepareTurn() {
        super.prepareTurn()
        if (turns == SUMMON_TURN) {
            if (brother == null || brother!!.dead) {
                summon()
            }
        } else {
            turns++
        }
    }

    private fun summon() {
        owner.getCommander()?.let {
            brother = BarbarianBrother(owner)
            game.spawn(brother!!, it.location)
        }
    }

    private inner class BarbarianBrother(owner: Player) : Soldier(owner) {
        init {
            setStats(2, 3)
            name = "Zweite Hand"
            icon = 49
        }

        override fun onDeath(throughBattle: BattleResult?) {
            turns = 0
        }
    }

    companion object {
        private const val SUMMON_TURN = 2
    }
}
