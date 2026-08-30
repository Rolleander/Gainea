package com.broll.gainea.server.core.utils

import com.broll.gainea.core.protocol.TextInfoType
import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.net.proto.NT_Event_TextInfo
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player

fun Game.gameLog(text: String) {
    sendUpdate(NT_Event_TextInfo.newBuilder().setType(TextInfoType.LOG.value).setText(text).build())
}

fun Game.displayMessage(text: String, sound: String? = null) {
    val info = NT_Event_TextInfo.newBuilder().setType(TextInfoType.DISPLAY.value).setText(text)
    sound?.let {
        info.setBase(NT_Event.newBuilder().setSound(it).build())
    }
    sendUpdate(info.build())
}

fun Player.displayMessage(text: String) {
    serverPlayer.send(
        NT_Event_TextInfo.newBuilder().setType(TextInfoType.DISPLAY.value).setText(text).build()
    )
}

fun Player.displayConfirmMessage(text: String) {
    serverPlayer.send(
        NT_Event_TextInfo.newBuilder().setType(TextInfoType.CONFIRM.value).setText(text).build()
    )
}
