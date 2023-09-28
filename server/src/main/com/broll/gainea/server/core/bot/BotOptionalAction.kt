package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Action

abstract class BotOptionalAction<A : NT_Action, O : BotOptionalAction.BotOption> : BotAction<A>() {
    var selectedOption: O? = null
        private set

    abstract fun score(action: A): O?
    fun setSelectedOption(selectedOption: O) {
        this.selectedOption = selectedOption
    }

    open class BotOption(val score: Float)
}
