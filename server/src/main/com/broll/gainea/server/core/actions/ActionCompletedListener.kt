package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action

interface ActionCompletedListener<T : ActionContext<out NT_Action>> {
    fun completed(context: T)
}
