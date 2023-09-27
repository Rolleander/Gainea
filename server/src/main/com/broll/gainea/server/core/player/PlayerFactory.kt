package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.LobbyPlayer

object PlayerFactory {
    fun create(game: GameContainer, serverPlayers: Collection<LobbyPlayer<PlayerData?>>): List<Player> {
        val players: MutableList<Player> = ArrayList()
        var color = 0
        val iterator = serverPlayers.iterator()
        while (iterator.hasNext()) {
            val player = create(game, iterator.next())
            player.setColor(color)
            players.add(player)
            color++
            game.updateReceiver.register(player.fraction)
        }
        return players
    }

    fun create(game: GameContainer, serverPlayer: LobbyPlayer<PlayerData?>): Player {
        val data = serverPlayer.data
        val fraction = data.getFraction().create()
        val player = Player(game, fraction, serverPlayer)
        fraction!!.init(game, player)
        return player
    }
}
