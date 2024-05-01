package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.OTHER
import com.google.common.collect.Lists

class C_Knowledge : Card(
    29, OTHER,
    "Bürokratie",
    "Wählt einer der folgenden Effekte: \n\n- Platziert einen Soldaten \n- Erhaltet " + STARS + " Sterne \n- Erhaltet eine zufällige Aktionskarte"
) {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val option = selectHandler.selection(
            "Wählt einen Effekt",
            Lists.newArrayList(
                "Platziert einen Soldaten",
                "Erhaltet " + STARS + " Sterne",
                "Erhaltet eine zufällige Aktionskarte"
            )
        )
        when (option) {
            0 -> owner.fraction.placeSoldier()
            1 -> owner.goalHandler.addStars(STARS)
            2 -> owner.cardHandler.drawRandomCard()
        }
    }

    companion object {
        private const val STARS = 5
    }
}
