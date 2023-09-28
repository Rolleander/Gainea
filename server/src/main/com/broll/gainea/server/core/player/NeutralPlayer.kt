package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.fractions.impl.DruidsFraction
import com.broll.gainea.server.init.PlayerData

class NeutralPlayer(game: GameContainer) : Player(game, DruidsFraction(), NeutralServerPlayer)

fun Player.isNeutral() = this is NeutralPlayer

object NeutralServerPlayer : com.broll.networklib.server.impl.LobbyPlayer<PlayerData>(null) {
    override fun isOnline() = false
}