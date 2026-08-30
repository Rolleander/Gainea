package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.proto.NT_Action_Card
import com.broll.gainea.net.proto.NT_Reaction
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.CardOption
import com.broll.gainea.server.core.bot.strategy.ICardStrategy
import com.google.protobuf.Message

class BotCard : BotOptionalAction<NT_Action_Card, CardOption>() {
    override fun react(action: NT_Action_Card, reaction: NT_Reaction.Builder) {
        val select = handler.getActionHandler(BotSelect::class.java)
        select.clearSelections()
        selectedOption!!.selectOptions.forEach { select.nextChooseOption(it) }
    }

    override val actionClass: Class<out Message>
        get() = NT_Action_Card::class.java

    override fun score(action: NT_Action_Card): CardOption? {
        val select = handler.getActionHandler(BotSelect::class.java)
        val card = bot.cardHandler.cards.find { it.id == action.cardId }
        return if (card is ICardStrategy) {
            (card as ICardStrategy).strategy(strategy, select)
        } else null
    }
}
