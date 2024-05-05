package com.broll.gainea.server.sites

import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.endPlayersTurn
import com.broll.gainea.server.core.utils.gameLog
import com.broll.gainea.server.init.ContentLibrary
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.IServerLobbyListener
import com.broll.networklib.server.impl.Player
import com.broll.networklib.server.impl.ServerLobby

class LobbyListener : IServerLobbyListener<LobbyData, PlayerData> {
    override fun playerJoined(
        lobby: ServerLobby<LobbyData, PlayerData>,
        player: Player<PlayerData>
    ) {
        var playerData = player.data
        if (playerData == null) {
            playerData = PlayerData(findOpenFraction(lobby))
            player.data = playerData
        }
        player.sendTCP(ContentLibrary.nt)
    }

    private fun findOpenFraction(lobby: ServerLobby<LobbyData, PlayerData>): FractionType {
        return FractionType.entries.subtract(lobby.players.filter { it.data != null }
            .map { it.data.fraction }.distinct()).randomOrNull()
            ?: FractionType.entries.random()
    }

    override fun playerLeft(
        lobby: ServerLobby<LobbyData, PlayerData>,
        player: Player<PlayerData>
    ) {
        lobby.data.game?.endPlayersTurn(player.data.gamePlayer)
    }

    override fun playerDisconnected(
        lobby: ServerLobby<LobbyData, PlayerData>,
        player: Player<PlayerData>
    ) {
        lobby.data.game?.let { game ->
            game.gameLog("Verbindung zu " + player.name + " verloren!")
            if (game.battleHandler.isBattleActive) {
                //try retreat from fight so battle does not get stuck
                game.battleHandler.playerReaction(player.data.gamePlayer, NT_Battle_Reaction())
            }
        }
    }

    override fun playerReconnected(
        lobby: ServerLobby<LobbyData, PlayerData>,
        player: Player<PlayerData>
    ) {
        lobby.data.game?.let { game ->
            game.processingCore.executeParallel({
                game.gameLog(player.name + " ist zurück!")
                //send game reconnect update to reconnecting player
                val gamePlayer = player.data.gamePlayer
                player.sendTCP(game.reconnect(gamePlayer))
                ProcessingUtils.pause(200)
                player.sendTCP(ContentLibrary.nt)
            }, 100)
        }
    }

    override fun lobbyClosed(lobby: ServerLobby<LobbyData, PlayerData>) {}
}
