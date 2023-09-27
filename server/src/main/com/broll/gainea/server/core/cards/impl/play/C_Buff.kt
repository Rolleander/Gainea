package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.bot.CardOptionimport com.broll.gainea.server.core.bot.impl .BotSelectimport com.broll.gainea.server.core.bot.strategy.BotStrategyimport com.broll.gainea.server.core.bot.strategy.ICardStrategyimport com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.utils.UnitControl
class C_Buff : Card(30, "Aufstieg", "Verleiht einer eurer Einheiten +1/+1"), ICardStrategy {
    override val isPlayable: Boolean
        get() = !owner.units.isEmpty()

    override fun play() {
        val unit: Unit = selectPlayerUnit(game, owner, "Welche Einheit soll gestärkt werden?")
        if (unit != null) {
            unit.power.addValue(1)
            unit.changeHealth(1)
            UnitControl.focus(game, unit, NT_Event.EFFECT_BUFF)
        }
    }

    override fun strategy(strategy: BotStrategy?, select: BotSelect?): CardOption {
        val option = CardOption()
        option.selectStrongestUnit(select)
        return option
    }
}
