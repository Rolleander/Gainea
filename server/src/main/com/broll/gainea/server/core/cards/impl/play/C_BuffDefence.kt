package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.bot.CardOption
import com.broll.gainea.server.core.bot.impl.BotSelect
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.bot.strategy.ICardStrategy
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.SelectionUtils.selectPlayerUnit
import com.broll.gainea.server.core.utils.UnitControl

class C_BuffDefence : Card(52, "Schildformation", "Verleiht einer Einheit +" + BUFF + " Leben"), ICardStrategy {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val unit = selectPlayerUnit(game, owner, "Welche Einheit soll ausgerüstet werden?")
        if (unit != null) {
            unit.changeHealth(BUFF)
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
        }
    }

    override fun strategy(strategy: BotStrategy, select: BotSelect): CardOption {
        //todo: must pick location first sometimes...
        val option = CardOption()
        option.selectStrongestUnit(select)
        return option
    }

    companion object {
        private const val BUFF = 2
    }
}
