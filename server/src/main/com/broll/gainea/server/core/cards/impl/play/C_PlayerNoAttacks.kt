package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.GlobalBuff
import com.broll.gainea.server.core.objects.buffs.IntBuff

class C_PlayerNoAttacks : Card(14, "Wachpatrouille", "Wählt einen Gegner, dieser kann " + DURATION + " Runden keine Angriffe ausführen.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val player = selectHandler.selectOtherPlayer(owner, "Welcher Spieler darf nicht mehr angreifen?")
        val buff = IntBuff(BuffType.SET, 0)
        GlobalBuff.createForPlayer(game, player, buff, { it.attacksPerTurn.addBuff(buff) }, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(buff, DURATION)
    }

    companion object {
        private const val DURATION = 3
    }
}
