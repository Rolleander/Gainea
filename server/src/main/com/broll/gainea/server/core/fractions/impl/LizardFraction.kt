package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.LocationUtils

class LizardFraction : Fraction(FractionType.LIZARDS) {
    private var turns = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Einheiten können Laufen und Angreifen im gleichen Zug")
        desc.contra("Erhält nur jeden zweiten Zug einen Soldat")
        desc.contra("Im Schnee -1 Zahl")
        return desc
    }

    override fun prepareTurn(actionHandlers: ActionHandlers) {
        turns++
        if (turns >= SPAWN_TURN || owner.controlledLocations.isEmpty()) {
            //spawn  soldier
            super.prepareTurn(actionHandlers)
            turns = 0
        }
    }

    override fun powerMutatorArea(power: FightingPower, area: Area) {
        if (LocationUtils.isAreaType(area, AreaType.SNOW)) {
            power.changeNumberPlus(-1)
        }
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(2, 2)
        soldier.name = "Echsenkrieger"
        soldier.icon = 122
        soldier.isMoveOrAttackRestriction = false
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(3, 5)
        commander.name = "Grash der Vernichter"
        commander.icon = 123
        commander.isMoveOrAttackRestriction = false
        return commander
    }

    companion object {
        private const val SPAWN_TURN = 2
    }
}
