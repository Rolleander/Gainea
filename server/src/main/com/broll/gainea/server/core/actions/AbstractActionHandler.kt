package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.ProcessingBlock

abstract class AbstractActionHandler<T : NT_Action, C : ActionContext<out T>> {
    protected lateinit var game: Game
    protected lateinit var player: Player
    protected lateinit var reactionResult: ReactionActions
    protected lateinit var actionHandlers: ActionHandlers
    protected val processingBlock = ProcessingBlock()

    fun init(game: Game, reactionResult: ReactionActions, actionHandlers: ActionHandlers) {
        this.game = game
        this.reactionResult = reactionResult
        this.actionHandlers = actionHandlers
    }

    fun update(player: Player) {
        this.player = player
    }

    fun skip() {
        processingBlock.resume()
    }

    abstract fun handleReaction(context: C, action: T, reaction: NT_Reaction)
}

