package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.isAreaType

class LizardFraction : Fraction(FractionType.LIZARDS) {
    private var turns = 0

    override val description: FractionDescription =
        FractionDescription(
            "",
            soldier = UnitDescription(name = "Echsenkrieger", icon = 122, power = 2, health = 2),
            commander = UnitDescription(
                name = "Grash der Vernichter",
                icon = 123,
                power = 3,
                health = 5
            ),
        ).apply {
            plus("Einheiten können Laufen und Angreifen im gleichen Zug")
            contra("Erhält nur jeden zweiten Zug einen Soldat")
            contra("Im Schnee -1 Zahl")
        }


    override fun prepareTurn() {
        turns++
        if (turns >= SPAWN_TURN || owner.controlledLocations.isEmpty()) {
            //spawn  soldier
            placeSoldier()
            turns = 0
        }
        prepareUnits()
    }

    override fun powerMutatorArea(power: FightingPower, area: Area) {
        if (area.isAreaType(AreaType.SNOW)) {
            power.changeNumberPlus(-1)
        }
    }

    override fun createCommander(): Soldier = super.createCommander().apply {
        isMoveOrAttackRestriction = false
    }

    override fun createSoldier(): Soldier = super.createSoldier().apply {
        isMoveOrAttackRestriction = false
    }

    companion object {
        private const val SPAWN_TURN = 2
    }
}
