package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Reactionimport com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.bot.strategy.BotStrategy com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

abstract class BotAction<A : NT_Action?> {
    protected var game: GameContainer? = null
    protected var bot: Player? = null
    protected var strategy: BotStrategy? = null
    protected var handler: BotActionHandler? = null
    fun init(game: GameContainer?, bot: Player?, strategy: BotStrategy?, handler: BotActionHandler?) {
        this.game = game
        this.bot = bot
        this.strategy = strategy
        this.handler = handler
    }

    protected abstract fun react(action: A, reaction: NT_Reaction)
    fun perform(action: A): NT_Reaction {
        val reaction = NT_Reaction()
        reaction.actionId = action!!.actionId
        react(action, reaction)
        return reaction
    }

    abstract val actionClass: Class<out NT_Action?>?
}
