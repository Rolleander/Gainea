package com.broll.gainea.server.sites

import com.broll.gainea.net.proto.NT_Battle_Reaction
import com.broll.gainea.core.fractions.FractionType
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.endPlayersTurn
import com.broll.gainea.server.core.utils.gameLog
import com.broll.gainea.server.init.ContentLibrary
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.playerData
import com.broll.gainea.server.network.players
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.lobby.LobbyUsersListener
import com.broll.mpnll.server.user.User

class LobbyListener : LobbyUsersListener {
    override fun userJoined(
        lobby: Lobby,
        player: User
    ) {
        if (player.data == null) {
            player.data = PlayerData(findOpenFraction(lobby))
        }
        player.send(ContentLibrary.nt)
    }

    private fun findOpenFraction(lobby: Lobby): FractionType {
        return FractionType.entries.subtract(lobby.players.filter { it.data != null }
            .map { it.playerData.fraction }.distinct()).randomOrNull()
            ?: FractionType.entries.random()
    }

    override fun userLeft(
        lobby: Lobby,
        player: User
    ) {
        lobby.gameData.game?.endPlayersTurn(player.playerData.gamePlayer)
    }

    override fun userDisconnected(
        lobby: Lobby,
        player: User
    ) {
        lobby.gameData.game?.let { game ->
            game.gameLog("Verbindung zu " + player.name + " verloren!")
            if (game.battleHandler.isBattleActive) {
                //try retreat from fight so battle does not get stuck
                game.battleHandler.playerReaction(player.playerData.gamePlayer, NT_Battle_Reaction.getDefaultInstance())
            }
        }
    }

    override fun userReconnected(
        lobby: Lobby,
        player: User
    ) {
        lobby.gameData.game?.let { game ->
            game.processingCore.executeParallel({
                game.gameLog(player.name + " ist zurück!")
                //send game reconnect update to reconnecting player
                val gamePlayer = player.playerData.gamePlayer
                player.send(game.reconnect(gamePlayer))
                ProcessingUtils.pause(200)
                player.send(ContentLibrary.nt)
            }, 100)
        }
    }
}
