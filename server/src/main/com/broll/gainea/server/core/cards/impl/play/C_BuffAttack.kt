package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.bot.CardOptionimport com.broll.gainea.server.core.bot.impl .BotSelectimport com.broll.gainea.server.core.bot.strategy.BotStrategyimport com.broll.gainea.server.core.bot.strategy.ICardStrategyimport com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.utils.UnitControl
class C_BuffAttack : Card(51, "Aufrüsten", "Verleiht einer Einheit +2 Angriff"), ICardStrategy {
    override val isPlayable: Boolean
        get() = !owner.units.isEmpty()

    override fun play() {
        val unit: Unit = selectPlayerUnit(game, owner, "Welche Einheit soll ausgerüstet werden?")
        unit.power.addValue(2)
        UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
    }

    override fun strategy(strategy: BotStrategy?, select: BotSelect?): CardOption {
        val option = CardOption()
        option.selectStrongestUnit(select)
        return option
    }
}
