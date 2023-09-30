package com.broll.gainea.server.core.utils

import com.broll.gainea.net.NT_Event_TextInfo
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player

fun Game.gameLog(text: String) {
    val info = NT_Event_TextInfo()
    info.type = NT_Event_TextInfo.TYPE_MESSAGE_LOG
    info.text = text
    sendUpdate(info)
}

fun Game.displayMessage(text: String) {
    val info = NT_Event_TextInfo()
    info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY
    info.text = text
    sendUpdate(info)
}

fun Player.displayMessage(text: String) {
    val info = NT_Event_TextInfo()
    info.type = NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY
    info.text = text
    serverPlayer.sendTCP(info)
}

fun Player.displayConfirmMessage(text: String) {
    val info = NT_Event_TextInfo()
    info.type = NT_Event_TextInfo.TYPE_CONFIRM_MESSAGE
    info.text = text
    serverPlayer.sendTCP(info)
}