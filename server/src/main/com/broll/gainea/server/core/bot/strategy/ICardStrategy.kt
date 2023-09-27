package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.bot.CardOptionimport

com.broll.gainea.server.core.bot.impl .BotSelect
interface ICardStrategy {
    fun strategy(strategy: BotStrategy?, select: BotSelect?): CardOption
}
