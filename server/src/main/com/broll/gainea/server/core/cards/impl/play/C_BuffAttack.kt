package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.bot.CardOption
import com.broll.gainea.server.core.bot.impl.BotSelect
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.bot.strategy.ICardStrategy
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.SelectionUtils.selectPlayerUnit
import com.broll.gainea.server.core.utils.UnitControl

class C_BuffAttack : Card(51, "Aufrüsten", "Verleiht einer Einheit +2 Angriff"), ICardStrategy {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val unit = selectPlayerUnit(game, owner, "Welche Einheit soll ausgerüstet werden?")
        if (unit != null) {
            unit.power.addValue(2)
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
        }
    }

    override fun strategy(strategy: BotStrategy, select: BotSelect): CardOption {
        val option = CardOption()
        option.selectStrongestUnit(select)
        return option
    }
}
