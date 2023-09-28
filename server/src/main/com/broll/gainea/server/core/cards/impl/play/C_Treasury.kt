package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card

class C_Treasury : Card(3, "Reichtum", "Erhaltet ein weiteres Ziel") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        game.goalStorage.assignNewRandomGoal(owner)
    }
}
