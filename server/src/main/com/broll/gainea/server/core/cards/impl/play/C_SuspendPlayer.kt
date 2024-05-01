package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.OTHER

class C_SuspendPlayer :
    Card(12, OTHER, "In den Kerker", "Ein Spieler deiner Wahl muss eine Runde aussetzen") {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        selectHandler.selectOtherPlayer(owner, "Spieler der Aussetzen muss:").skipRounds(1)
    }
}
