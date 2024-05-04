package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.utils.endTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.network.nt.NT_ChatMessage
import com.broll.networklib.server.impl.LobbySite

class CustomLobbySite : LobbySite<LobbyData, PlayerData>() {
    val CMD_PREIFX = "/"
    val CMDS = listOf(
        "nextturn" to { game: Game ->
            game.battleHandler.reset()
            game.endTurn()
        }
    )

    val game: Game?
        get() = lobby.data.game

    //todo check if it still works
    override fun chat(chatMessage: NT_ChatMessage) {
        //check for commands
        val cmd = CMDS.find { CMD_PREIFX + it.first == chatMessage.message.trim().lowercase() }
        if (game != null && cmd != null) {
            cmd.second(game!!)
            return;
        }
        super.chat(chatMessage)
    }

}

