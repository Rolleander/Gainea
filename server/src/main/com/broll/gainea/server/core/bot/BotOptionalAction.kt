package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.server.core.bot.BotOptionalAction.BotOption
abstract class BotOptionalAction<A : NT_Action?, O : BotOption?> : BotAction<A>() {
    var selectedOption: O? = null
        private set

    abstract fun score(action: A): O
    fun setSelectedOption(selectedOption: O) {
        this.selectedOption = selectedOption
    }

    open class BotOption(val score: Float)
}
