package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_Card
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.CardOption
import com.broll.gainea.server.core.bot.strategy.ICardStrategy

class BotCard : BotOptionalAction<NT_Action_Card, CardOption>() {
    override fun react(action: NT_Action_Card, reaction: NT_Reaction) {
        val select = handler.getActionHandler(BotSelect::class.java)
        select.clearSelections()
        selectedOption!!.getSelectOptions().forEach { select.nextChooseOption(it) }
    }

    override val actionClass: Class<out NT_Action>
        get() = NT_Action_Card::class.java

    override fun score(action: NT_Action_Card): CardOption? {
        val select = handler.getActionHandler(BotSelect::class.java)
        val card = bot.cardHandler.cards.find { it.id == action.cardId }
        return if (card is ICardStrategy) {
            (card as ICardStrategy).strategy(strategy, select)
        } else null
    }
}
