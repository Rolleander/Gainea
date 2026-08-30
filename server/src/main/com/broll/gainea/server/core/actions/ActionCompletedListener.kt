package com.broll.gainea.server.core.actions

import com.google.protobuf.Message

interface ActionCompletedListener<T : ActionContext<out Message>> {
    fun completed(context: T)
}
