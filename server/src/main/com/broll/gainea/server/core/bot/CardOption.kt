package com.broll.gainea.server.core.bot

import com.broll.gainea.server.core.bot.impl.BotSelect

class CardOption : BotOptionalAction.BotOption {
    private val selectOptions = mutableListOf<BotSelect.Selection>()

    constructor(score: Float) : super(score)
    constructor() : super(20f)

    fun select(option: BotSelect.Selection) {
        selectOptions.add(option)
    }

    fun selectStrongestUnit(select: BotSelect) {
        selectOptions.add(select.selectUnit { units -> BotUtils.getHighestScoreEntry(units) { it.battleStrength } })
    }

    fun getSelectOptions(): List<BotSelect.Selection> {
        return selectOptions
    }
}
