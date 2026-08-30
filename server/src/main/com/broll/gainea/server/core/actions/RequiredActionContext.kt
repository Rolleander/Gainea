package com.broll.gainea.server.core.actions

import com.broll.gainea.net.proto.NT_PlayerAction
import com.google.protobuf.Message

class RequiredActionContext<T : Message>
(val actionContext: ActionContext<T>, val text: String, val messageForOtherPlayer: Message? = null)
    : ActionContext<T>(actionContext.action) {

    override fun assignActionId(id: Int) {
        actionContext.assignActionId(id)
        action = actionContext.action
    }

    fun nt(): NT_PlayerAction {
        return NT_PlayerAction.newBuilder().setAction(action.asAction())
            .setText(text).build()
    }
}
