package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.Ship
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.spawn

class DruidsFraction : Fraction(FractionType.DRUIDS) {
    override val description: FractionDescription =
        FractionDescription(
            "",
            soldier = UnitDescription(name = "Druidin", icon = 110),
            commander = UnitDescription(
                name = "Druidenhaupt Zerus",
                icon = 102,
                power = 3,
                health = 3
            ),
        ).apply {
            plus("Gefallene Einheiten können zu unbeweglichen Wurzelgolems (1/2) werden")
            contra("Auf Schiffen -1 Zahl")
        }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        if (context.location is Ship) {
            power.changeNumberPlus(-1)
        }
        return power
    }

    override fun createCommander(): Soldier {
        val commander = DruidSoldier(owner)
        commander.commander = true
        commander.initFrom(description.commander)
        return commander
    }
    
    override fun createSoldier(): Soldier {
        val soldier = DruidSoldier(owner)
        soldier.initFrom(description.soldier)
        soldier.setType(NT_Unit.TYPE_FEMALE.toInt())
        return soldier
    }

    private inner class DruidSoldier(owner: Player) :
        Soldier(owner, fraction = this@DruidsFraction) {
        override fun onDeath(throughBattle: BattleResult?) {
            if (RandomUtils.randomBoolean(SPAWN_CHANCE)) {
                val tree = Tree(owner)
                game.spawn(tree, location)
            }
        }
    }

    private inner class Tree(owner: Player) : Monster(owner) {
        init {
            setStats(1, 2)
            name = "Wurzelgolem"
            icon = 99
            description = "Unbeweglich"
            //cant move or attack
            attacksPerTurn.value = 0
            movesPerTurn.value = 0
        }
    }

    companion object {
        private const val SPAWN_CHANCE = 0.4f
    }
}
