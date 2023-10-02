package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.player.Player


abstract class BotAction<A : NT_Action> {
    protected lateinit var game: Game
    protected lateinit var bot: Player
    protected lateinit var strategy: BotStrategy
    protected lateinit var handler: BotActionHandler

    fun init(game: Game, bot: Player, strategy: BotStrategy, handler: BotActionHandler) {
        this.game = game
        this.bot = bot
        this.strategy = strategy
        this.handler = handler
    }

    protected abstract fun react(action: A, reaction: NT_Reaction)
    fun perform(action: A): NT_Reaction {
        val reaction = NT_Reaction()
        reaction.actionId = action.actionId
        react(action, reaction)
        return reaction
    }

    abstract val actionClass: Class<out NT_Action>
}
