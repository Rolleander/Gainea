package com.broll.gainea.server.core.actions

import com.google.protobuf.Message

abstract class ActionContext<T : Message>(action: T) {
    var action: T
    var customHandler: CustomReactionHandler<ActionContext<out Message>>? = null
    var completionListener: ActionCompletedListener<ActionContext<out Message>>? = null

    init {
        this.action = action
    }

    open fun assignActionId(id: Int) {
        action = action.withActionId(id)
    }
}
