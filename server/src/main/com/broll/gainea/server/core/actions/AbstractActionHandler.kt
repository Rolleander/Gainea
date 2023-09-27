package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.ProcessingBlock

abstract class AbstractActionHandler<T : NT_Action?, C> {
    protected lateinit var game: GameContainer
    protected lateinit var player: Player
    protected lateinit var reactionResult: ReactionActions
    protected lateinit var actionHandlers: ActionHandlers
    protected var processingBlock = ProcessingBlock()
    fun init(game: GameContainer, reactionResult: ReactionActions, actionHandlers: ActionHandlers) {
        this.game = game
        this.reactionResult = reactionResult
        this.actionHandlers = actionHandlers
    }

    fun update(player: Player) {
        this.player = player
    }

    abstract fun handleReaction(context: C, action: T, reaction: NT_Reaction)
}
