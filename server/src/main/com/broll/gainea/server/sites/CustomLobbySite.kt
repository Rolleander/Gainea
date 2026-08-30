package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.utils.skipTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.playerData
import com.broll.gainea.server.network.systemMessage
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.lobby.LobbyChatHandler
import com.broll.mpnll.server.user.User

class CustomLobbySite : LobbyChatHandler {
    val CMD_PREIFX = "/"
    val CMDS = listOf(
        "help" to this::help,
        "nextturn" to { game: Game, player: User ->
            game.skipTurn()
        },
        "drawcard" to { game: Game, player: User ->
            player.playerData.gamePlayer.cardHandler.drawRandomPlayableCard()
        }
    )

    private fun help(game: Game, player: User) {
        val cmds = CMDS.takeLast(CMDS.size - 1).joinToString(",") { "$CMD_PREIFX${it.first}" }
        player.lobby.systemMessage(cmds, player)
    }

    override fun handle(lobby: Lobby, sender: User, message: String): Boolean {
        val game = lobby.gameData.game
        val cmd = CMDS.find { CMD_PREIFX + it.first == message.trim().lowercase() }
        if (game != null && cmd != null) {
            if (lobby.owner == sender) {
                cmd.second(game, sender)
            } else {
                lobby.systemMessage("Only lobby owner is allowed to send commands", sender)
            }
            return true
        }
        return false
    }
}
