package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.PlayerUtils

class BarbarianFraction : Fraction(FractionType.BARBARIANS) {
    private var turns = 0
    private var brother: BarbarianBrother? = null
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Nach " + SUMMON_TURN + " Runden ruft der Kommandant seine zweite Hand (2/3) herbei")
        desc.plus("+1 Zahl, wenn Kommandant und zweite Hand zusammen kämpfen")
        desc.contra("-1 Zahl, wenn keine Barbarenrkieger im Kampf beteiligt sind")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext?): FightingPower? {
        val power = super.calcFightingPower(soldier, context)
        val army = context!!.getFightingArmy(soldier)
        if (army!!.stream().noneMatch { unit: Unit? -> isPlainBarbarianSoldier(unit) }) {
            power!!.changeNumberPlus(-1)
        }
        if (army.stream().anyMatch { obj: Unit? -> PlayerUtils.isCommander() }
                && army.stream().anyMatch { it: Unit? -> it is BarbarianBrother }) {
            power!!.changeNumberPlus(1)
        }
        return power
    }

    private fun isPlainBarbarianSoldier(unit: Unit?): Boolean {
        return unit is Soldier && !unit.isCommander &&
                unit !is BarbarianBrother && unit.fraction === this
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Barbarenrkieger"
        soldier.icon = 103
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Barbarenanführer"
        commander.icon = 45
        return commander
    }

    override fun prepareTurn(actionHandlers: ActionHandlers?) {
        super.prepareTurn(actionHandlers)
        if (turns == SUMMON_TURN) {
            if (brother == null || brother!!.isDead) {
                summon()
            }
        } else {
            turns++
        }
    }

    private fun summon() {
        PlayerUtils.getCommander(owner).ifPresent { commander: Soldier? ->
            brother = BarbarianBrother(owner)
            spawn(game, brother, commander.getLocation())
        }
    }

    private inner class BarbarianBrother(owner: Player?) : Soldier(owner) {
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
        private const val SUMMON_TURN = 3
    }
}
