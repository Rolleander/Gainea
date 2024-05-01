package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.OTHER
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.recruit
import com.broll.gainea.server.core.utils.selectUnitFromLocations

class C_MonsterRecruit :
    Card(71, OTHER, "Tierfreund", "Rekrutiert ein benachbartes Monster mit maximal 4 Sternen") {
    init {
        drawChance = 0.8f
    }

    override val isPlayable: Boolean
        get() = !monsterLocations.isEmpty()

    private fun validMonster(obj: MapObject?) =
        obj is Monster && obj.owner.isNeutral() && obj.stars <= 4


    private val monsterLocations: Collection<Location>
        get() = owner.controlledLocations.flatMap { it.connectedLocations }.distinct()
            .filter { it.inhabitants.any { inhabitant -> validMonster(inhabitant) } }


    override fun play() {
        val monsterLocations = monsterLocations
        if (!monsterLocations.isEmpty()) {
            val monster = game.selectUnitFromLocations(
                monsterLocations, { validMonster(it) },
                "Welches Monster soll rekrutiert werden?"
            ) ?: return
            game.recruit(owner, listOf(monster))
        }
    }
}
