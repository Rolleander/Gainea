package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action

class NoActionContext<T : NT_Action?>(action: T) : ActionContext<T>(action) {
    constructor(action: T, handler: CustomReactionHandler<NoActionContext<*>?>?) : this(action) {
        customHandler = handler
    }
}
