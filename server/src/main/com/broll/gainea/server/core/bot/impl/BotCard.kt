package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Action_Cardimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.server.core.bot.BotOptionalActionimport com.broll.gainea.server.core.bot.CardOptionimport com.broll.gainea.server.core.bot.strategy.ICardStrategyimport com.broll.gainea.server.core.cards.Cardimport java.util.function.Consumer
class BotCard : BotOptionalAction<NT_Action_Card, CardOption?>() {
    override fun react(action: NT_Action_Card, reaction: NT_Reaction) {
        val select = handler!!.getActionHandler(BotSelect::class.java) as BotSelect
        select.clearSelections()
        selectedOption.getSelectOptions().forEach(Consumer { option: BotSelect.Selection? -> select.nextChooseOption(option) })
    }

    override val actionClass: Class<out NT_Action?>?
        get() = NT_Action_Card::class.java

    override fun score(action: NT_Action_Card): CardOption? {
        val select = handler!!.getActionHandler(BotSelect::class.java) as BotSelect
        val card = bot.cardHandler.cards.stream().filter { it: Card? -> it.getId() == action.cardId }.findFirst().orElse(null)
        return if (card is ICardStrategy) {
            (card as ICardStrategy).strategy(strategy, select)
        } else null
    }
}
