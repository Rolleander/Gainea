package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.utils.skipTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.network.nt.NT_ChatMessage
import com.broll.networklib.server.impl.LobbySite
import com.broll.networklib.server.impl.Player

class CustomLobbySite : LobbySite<LobbyData, PlayerData>() {
    val CMD_PREIFX = "/"
    val CMDS = listOf(
        "help" to this::help,
        "nextturn" to { game: Game, player: Player<PlayerData> ->
            game.skipTurn()
        },
        "drawcard" to { game: Game, player: Player<PlayerData> ->
            player.data.gamePlayer.cardHandler.drawRandomPlayableCard()
        }
    )

    val game: Game?
        get() = lobby.data.game

    private fun help(game: Game, player: Player<PlayerData>) {
        val cmds = CMDS.takeLast(CMDS.size - 1).joinToString(",") { "$CMD_PREIFX${it.first}" }
        player.sendResponse(cmds)
    }

    override fun chat(chatMessage: NT_ChatMessage) {
        val cmd = CMDS.find { CMD_PREIFX + it.first == chatMessage.message.trim().lowercase() }
        if (game != null && cmd != null) {
            if (lobby.owner == player) {
                cmd.second(game!!, player)
            } else {
                player.sendResponse("Only lobby owner is allowed to send commands")
            }
            return;
        }
        super.chat(chatMessage)
    }

    private fun Player<PlayerData>.sendResponse(msg: String) {
        val nt = NT_ChatMessage()
        nt.message = msg
        sendTCP(nt)
    }

}

