package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.damage

class C_Tremor : DirectlyPlayedCard(23, "Auge um Auge", "Verursacht 1 Schaden an einer zufälligen Einheit jedes Spielers") {
    override fun play() {
        PlayerUtils.iteratePlayers(game, 500) { player ->
            val unit = RandomUtils.pickRandom(player.units)
            if (unit != null) {
                //deal 1 damage
                damage(game, unit)
            }
        }
    }
}
