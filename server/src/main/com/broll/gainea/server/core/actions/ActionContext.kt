package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action

abstract class ActionContext<T : NT_Action>(action: T) {
    val action: T
    var customHandler: CustomReactionHandler<ActionContext<out NT_Action>>? = null
    var completionListener: ActionCompletedListener<ActionContext<out NT_Action>>? = null

    init {
        this.action = action
    }
}
