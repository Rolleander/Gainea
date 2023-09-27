package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_PlayerAction

class RequiredActionContext<T : NT_Action?> @JvmOverloads constructor(val actionContext: ActionContext<T>, val text: String?, val messageForOtherPlayer: Any? = null) : ActionContext<T>(actionContext.action) {

    fun nt(): NT_PlayerAction {
        val action = NT_PlayerAction()
        action.action = actionContext.action
        action.text = text
        return action
    }
}
