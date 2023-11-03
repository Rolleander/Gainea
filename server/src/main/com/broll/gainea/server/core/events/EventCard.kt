package com.broll.gainea.server.core.events

import com.broll.gainea.net.NT_Event_PlayedCard
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.optional.CardAction
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.sendUpdate

abstract class EventCard(picture: Int, title: String, text: String) :
    DirectlyPlayedCard(picture, title, text) {
    fun run(game: Game) {
        init(game, game.neutralPlayer, game.newObjectId())
        play(game.reactionHandler.actionHandlers)
    }

    companion object {
        fun run(event: EventCard, game: Game) {
            val playedCard = NT_Event_PlayedCard()
            playedCard.player = -1
            playedCard.card = event.nt()
            game.sendUpdate(playedCard)
            ProcessingUtils.pause(CardAction.PLAY_CARD_DELAY)
            event.run(game)
        }
    }
}
