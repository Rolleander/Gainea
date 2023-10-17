package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event_PlayedCard
import com.broll.gainea.server.core.actions.optional.CardAction
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.sendUpdate

class C_DirectPlay : Card(84, "Wahl des Druiden", "Wählt eine von drei Event-Karten, die direkt ausgespielt wird.") {

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val cards = (1..OPTIONS).map { game.cardStorage.getRandomDirectlyPlayedCard() }
        val card = cards[selectHandler.selectObject("Wählt eine Event-Karte", cards.map { it.nt() })]
        val playedCard = NT_Event_PlayedCard()
        playedCard.player = owner.serverPlayer.id
        playedCard.card = card.nt()
        game.sendUpdate(playedCard)
        ProcessingUtils.pause(CardAction.PLAY_CARD_DELAY)
        card.init(game, owner, game.newObjectId())
        card.play(game.reactionHandler.actionHandlers)
    }

    companion object {
        private const val OPTIONS = 3
    }
}
