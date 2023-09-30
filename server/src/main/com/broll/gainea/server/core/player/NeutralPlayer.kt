package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.FractionType.DRUIDS
import com.broll.gainea.server.core.fractions.impl.DruidsFraction
import com.broll.gainea.server.init.PlayerData

class NeutralPlayer(game: Game) : Player(game, DruidsFraction(), NeutralServerPlayer)

fun Player.isNeutral() = this is NeutralPlayer

object NeutralServerPlayer : com.broll.networklib.server.impl.LobbyPlayer<PlayerData>(DummyPlayer) {
    override fun isOnline() = false
}

object DummyPlayer : com.broll.networklib.server.impl.Player<PlayerData>(-1, "", null) {
    init {
        data = PlayerData(DRUIDS)
    }
}