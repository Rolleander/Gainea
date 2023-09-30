package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.displayConfirmMessage

class C_Secrets : Card(40, "Bestechung", "Schaut euch die Ziele und Aktionskarten eines beliebigen Spielers an") {
    init {
        drawChance = 0.8f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val player = selectHandler.selectOtherPlayer(owner, "Welchen Spieler bestechen?")
        val cards = player.cardHandler.cards
        val goals = player.goalHandler.goals
        val name = player.serverPlayer.name
        var text = "$name's Ziele: \n"
        for (goal in goals) {
            text += " - " + goal.text + " ( " + goal.difficulty.points + "P " + goal.restrictionInfo + ")\n"
        }
        text += "$name's Aktionskarten: \n"
        for (card in cards) {
            text += " - " + card.title + "\n"
        }
        owner.displayConfirmMessage(text)
    }
}
