package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_Card
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.playCard
import org.slf4j.LoggerFactory

class CardAction : AbstractActionHandler<NT_Action_Card, CardAction.Context>() {
    inner class Context(
        action: NT_Action_Card, val card: Card,
    ) : ActionContext<NT_Action_Card>(action)

    fun playableCard(card: Card): Context {
        val action = NT_Action_Card()
        action.cardId = card.id
        return Context(action, card)
    }

    override fun handleReaction(context: Context, action: NT_Action_Card, reaction: NT_Reaction) {
        game.processingCore.execute { playCard(context.card) }
    }

    private fun playCard(card: Card) {
        Log.trace("Handle card reaction")
        game.playCard(card)
    }

    companion object {
        const val PLAY_CARD_DELAY = 3500
        private val Log = LoggerFactory.getLogger(CardAction::class.java)
    }
}
