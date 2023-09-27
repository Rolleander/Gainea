package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Reaction

interface CustomReactionHandler<T : ActionContext<*>?> {
    fun handleReaction(context: T, reaction: NT_Reaction?)
}
