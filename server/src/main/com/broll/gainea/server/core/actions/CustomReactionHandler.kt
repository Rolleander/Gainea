package com.broll.gainea.server.core.actions

import com.broll.gainea.net.proto.NT_Reaction
import com.google.protobuf.Message

interface CustomReactionHandler<T : ActionContext<out Message>> {
    fun handleReaction(context: T, reaction: NT_Reaction)
}
