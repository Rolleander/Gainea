package com.broll.gainea.server.core.actions

import com.broll.gainea.server.core.player.Player

interface ReactionActions {
    fun endTurn()
    fun sendBoardUpdate()
    fun requireAction(player: Player?, action: RequiredActionContext<*>?): ActionContext<*>?
    fun sendGameUpdate(update: Any?)
}
