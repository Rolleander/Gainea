package com.broll.gainea.server.core.actions

import com.broll.gainea.server.core.player.Player
import com.google.protobuf.Message

interface ReactionActions {
    fun endTurn()
    fun sendBoardUpdate()
    fun requireAction(player: Player, action: RequiredActionContext<out Message>): ActionContext<out Message>
    fun sendGameUpdate(update: Message)
}
