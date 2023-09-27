package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtils
class C_Tremor : DirectlyPlayedCard(23, "Auge um Auge", "Verursacht 1 Schaden an einer zufälligen Einheit jedes Spielers") {
    override fun play() {
        PlayerUtils.iteratePlayers(game!!, 500) { player: Player? ->
            val unit = RandomUtils.pickRandom(player.getUnits())
            if (unit != null) {
                //deal 1 damage
                damage(game, unit, 1)
            }
        }
    }
}
