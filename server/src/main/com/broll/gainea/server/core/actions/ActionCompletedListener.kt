package com.broll.gainea.server.core.actions

interface ActionCompletedListener<T : ActionContext<*>?> {
    fun completed(context: T)
}
