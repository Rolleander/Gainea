package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.LobbyPlayer

object PlayerFactory {
    fun create(game: GameContainer, serverPlayers: Collection<LobbyPlayer<PlayerData>>) = serverPlayers.mapIndexed { index, lobbyPlayer ->
        val player = create(game, lobbyPlayer)
        player.setColor(index)
        game.updateReceiver.register(player.fraction)
        player
    }

    fun create(game: GameContainer, serverPlayer: LobbyPlayer<PlayerData>): Player {
        val data = serverPlayer.data
        val fraction = data.fraction.create()
        val player = Player(game, fraction, serverPlayer)
        fraction.init(game, player)
        return player
    }
}
