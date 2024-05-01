package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.MOVEMENT
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.utils.UnitControl.move

class C_TeleportUnknown : Card(
    80, MOVEMENT, "Schritte ins Unbekannte",
    "Teleportiert eine eurer Armeen auf ein zufälliges freies Feld eines beliebigen Kontinent"
) {

    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty() && targets.isNotEmpty()

    val targets: List<Location>
        get() = game.map.allContinents.flatMap { it.areas }.filter { it.free }

    override fun play() {
        val from =
            selectHandler.selectLocation("Wählt eine Armee", owner.controlledLocations.toList())
        val choices = targets.map { it.container.name }.distinct()
        val selected = selectHandler.selection("Zu welchem Kontinent?", choices)
        val to = targets.filter { it.container.name == choices[selected] }.random()
        game.move(from.units.filter { it.owner == owner }, to)
    }

}
