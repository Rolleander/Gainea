package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.UnitControl.heal

class C_HealingSpell : Card(62, "Lichtbeschwörung", "Heilt alle eure Einheiten") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        owner.units.filter { it.hurt }.forEach { game.heal(it, 10000) }
    }
}
