package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.getSpawnLocations
import com.broll.gainea.server.core.utils.isCommanderAlive

class C_ResurrectCommander :
    Card(61, "Auferstehung", "Lasst euren gefallenen Feldherr zurückkehren") {
    init {
        drawChance = 2f
    }

    override val isPlayable: Boolean
        get() = !owner.isCommanderAlive() && game.getSpawnLocations(owner).isNotEmpty()

    override fun play() {
        placeUnitHandler.placeUnit(
            owner,
            owner.fraction.createCommander(),
            game.getSpawnLocations(owner)
        )
    }
}
