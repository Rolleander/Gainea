package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_Card
import com.broll.gainea.net.NT_Event_PlayedCard
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.ProcessingUtils
import org.slf4j.LoggerFactory

class CardAction : AbstractActionHandler<NT_Action_Card, CardAction.Context>() {
    inner class Context(action: NT_Action_Card,  val card: Card ,
                        val player: Player) : ActionContext<NT_Action_Card>(action)

    fun playableCard(player: Player, card: Card): Context {
        val action = NT_Action_Card()
        action.cardId = card.id
        return Context(action, card, player)
    }

    override fun handleReaction(context: Context, action: NT_Action_Card, reaction: NT_Reaction) {
        game.processingCore.execute { playCard(context.player, context.card) }
    }

    fun playCard(player: Player, card: Card) {
        Log.trace("Handle card reaction")
        val playedCard = NT_Event_PlayedCard()
        playedCard.player = player.serverPlayer.id
        playedCard.card = card.nt()
        reactionResult.sendGameUpdate(playedCard)
        player.cardHandler.cards.remove(card)
        ProcessingUtils.pause(PLAY_CARD_DELAY)
        card.play(actionHandlers)
        game.updateReceiver.playedCard(card)
    }

    companion object {
        const val PLAY_CARD_DELAY = 5000
        private val Log = LoggerFactory.getLogger(CardAction::class.java)
    }
}
