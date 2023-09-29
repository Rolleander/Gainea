package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Reaction

interface CustomReactionHandler<T : ActionContext<NT_Action>> {
    fun handleReaction(context: T, reaction: NT_Reaction)
}
