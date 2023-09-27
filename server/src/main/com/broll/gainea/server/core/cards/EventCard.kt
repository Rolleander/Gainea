package com.broll.gainea.server.core.cards

abstract class EventCard(picture: Int, title: String, text: String) : DirectlyPlayedCard(picture, title, text) {
    fun run(game: GameContainer) {
        init(game, null, game.newObjectId())
        play(game.reactionHandler.actionHandlers)
    }

    companion object {
        private val Log = LoggerFactory.getLogger(EventCard::class.java)
        fun run(eventClass: Class<out EventCard>, game: GameContainer) {
            try {
                val event = eventClass.newInstance()
                val playedCard = NT_Event_PlayedCard()
                playedCard.player = -1
                playedCard.card = event.nt()
                game.reactionHandler.actionHandlers.reactionActions.sendGameUpdate(playedCard)
                ProcessingUtils.pause(CardAction.Companion.PLAY_CARD_DELAY)
                event.run(game)
            } catch (e: InstantiationException) {
                Log.error("Failed to instantiate event card $eventClass", e)
            } catch (e: IllegalAccessException) {
                Log.error("Failed to instantiate event card $eventClass", e)
            }
        }
    }
}
