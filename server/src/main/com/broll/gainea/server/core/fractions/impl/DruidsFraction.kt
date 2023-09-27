package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player

class DruidsFraction : Fraction(FractionType.DRUIDS) {
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Gefallene Einheiten können zu unbeweglichen Wurzelgolems (1/2) werden")
        desc.contra("Auf Schiffen -1 Zahl")
        return desc
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext?): FightingPower? {
        val power = super.calcFightingPower(soldier, context)
        if (context!!.location is Ship) {
            power!!.changeNumberPlus(-1)
        }
        return power
    }

    override fun createSoldier(): Soldier {
        val soldier: Soldier = DruidSoldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Druidin"
        soldier.icon = 110
        soldier.setType(NT_Unit.TYPE_FEMALE.toInt())
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = DruidSoldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Druidenhaupt Zerus"
        commander.icon = 102
        return commander
    }

    private inner class DruidSoldier(owner: Player?) : Soldier(owner) {
        override fun onDeath(throughBattle: BattleResult?) {
            if (RandomUtils.randomBoolean(SPAWN_CHANCE)) {
                val tree: Tree = Tree(getOwner())
                spawn(game, tree, location)
            }
        }
    }

    private inner class Tree(owner: Player?) : Soldier(owner) {
        init {
            setStats(1, 2)
            name = "Wurzelgolem"
            icon = 99
            //cant move or attack
            attacksPerTurn.value = 0
            getMovesPerTurn()!!.value = 0
        }
    }

    companion object {
        private const val SPAWN_CHANCE = 0.4f
    }
}
