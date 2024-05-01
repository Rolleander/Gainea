package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.bot.CardOption
import com.broll.gainea.server.core.bot.impl.BotSelect
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.bot.strategy.ICardStrategy
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.selectPlayerUnit

class C_BuffDefence :
    Card(52, EffectType.BUFF, "Schildformation", "Verleiht einer Einheit +$BUFF Leben"),
    ICardStrategy {
    override val isPlayable: Boolean
        get() = owner.units.isNotEmpty()

    override fun play() {
        val unit = game.selectPlayerUnit(owner, "Welche Einheit soll ausgerüstet werden?")
        if (unit != null) {
            unit.addHealth(BUFF)
            game.focus(unit, NT_Event.EFFECT_BUFF)
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
