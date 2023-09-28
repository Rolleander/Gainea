package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.SelectionUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_MonsterRecruit : Card(71, "Tierfreund", "Rekrutiert ein benachbartes Monster (ausser Götterdrache)") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = !monsterLocations.isEmpty()

    private fun validMonster(`object`: MapObject?) = `object` is Monster && `object`.owner.isNeutral() && `object` !is GodDragon


    private val monsterLocations: Collection<Location>
        get() = owner.controlledLocations.flatMap { it.connectedLocations }.distinct().filter { it.inhabitants.any { inhabitant -> validMonster(inhabitant) } }


    override fun play() {
        val monsterLocations = monsterLocations
        if (!monsterLocations.isEmpty()) {
            val monster = SelectionUtils.selectUnitFromLocations(game,
                    monsterLocations, { validMonster(it) },
                    "Welches Monster soll rekrutiert werden?") as Monster? ?: return
            owner.units.add(monster)
            monster.removeActionTimer()
            monster.owner = owner
            game.objects.remove(monster)
            UnitControl.focus(game, monster, 0)
        }
    }
}
