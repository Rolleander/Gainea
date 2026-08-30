package com.broll.gainea.server.core.bot

import com.google.protobuf.Message

abstract class BotOptionalAction<A : Message, O : BotOptionalAction.BotOption> : BotAction<A>() {
    var selectedOption: O? = null
        private set

    abstract fun score(action: A): O?
    fun setSelectedOption(selectedOption: O) {
        this.selectedOption = selectedOption
    }

    open class BotOption(val score: Float)
}
