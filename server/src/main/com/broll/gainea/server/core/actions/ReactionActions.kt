package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.server.core.player.Player

interface ReactionActions {
    fun endTurn()
    fun sendBoardUpdate()
    fun requireAction(player: Player, action: RequiredActionContext<out NT_Action>): ActionContext<out NT_Action>
    fun sendGameUpdate(update: Any)
}
