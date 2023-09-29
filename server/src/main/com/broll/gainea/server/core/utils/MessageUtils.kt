package com.broll.gainea.server.core.utils

import com.broll.gainea.net.NT_Event_TextInfo
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.player.Player

object MessageUtils {
    fun gameLog(game: GameContainer, text: String) {
        val info = NT_Event_TextInfo()
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_LOG
        info.text = text
        GameUtils.sendUpdate(game, info)
    }

    fun displayMessage(game: GameContainer, text: String) {
        val info = NT_Event_TextInfo()
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY
        info.text = text
        GameUtils.sendUpdate(game, info)
    }

    fun displayMessage(forPlayer: Player, text: String) {
        val info = NT_Event_TextInfo()
        info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY
        info.text = text
        forPlayer.serverPlayer.sendTCP(info)
    }

    fun displayConfirmMessage(forPlayer: Player, text: String) {
        val info = NT_Event_TextInfo()
        info.type = NT_Event_TextInfo.TYPE_CONFIRM_MESSAGE
        info.text = text
        forPlayer.serverPlayer.sendTCP(info)
    }
}
