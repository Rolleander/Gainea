package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.Unit
class C_HealingSpell : Card(62, "Lichtbeschwörung", "Heilt alle eure Einheiten") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        owner.units.stream().filter { obj: Unit? -> obj!!.isHurt }.forEach { unit: Unit? -> heal(game, unit, 10000) }
    }
}
