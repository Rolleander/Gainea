package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.create
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.playerData
import com.broll.mpnll.server.user.User

object PlayerFactory {
    fun create(game: Game, serverPlayers: Collection<User>) =
        serverPlayers.mapIndexed { index, lobbyPlayer ->
            val player = create(game, lobbyPlayer)
            player.setColor(index)
            game.updateReceiver.register(player.fraction)
            player
        }

    fun create(game: Game, serverPlayer: User): Player {
        val data = serverPlayer.playerData
        val fraction = data.fraction.create()
        return Player(game, fraction, serverPlayer)
    }
}
