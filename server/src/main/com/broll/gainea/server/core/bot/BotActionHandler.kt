package com.broll.gainea.server.core.bot

import com.broll.gainea.misc.PackageLoaderimport

com.broll.gainea.net.NT_Actionimport com.broll.gainea.net.NT_EndTurnimport com.broll.gainea.net.NT_PlayerTurnActionsimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.bot.BotOptionalAction.BotOptionimport com.broll.gainea.server.core.bot.strategy.BotStrategy com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

org.slf4j.LoggerFactoryimport java.lang.RuntimeExceptionimport java.util.HashMapimport java.util.function.Consumer
class BotActionHandler(private val game: GameContainer?, private val bot: Player?, private val strategy: BotStrategy) {
    private val actions: MutableMap<Class<out NT_Action?>?, BotAction<*>?> = HashMap()
    private val endTurnOption = EndTurnOption()

    init {
        PackageLoader(BotAction::class.java, PACKAGE_PATH).instantiateAll().forEach(Consumer { a: BotAction<*> -> initAction(a.actionClass, a) })
    }

    fun getActionHandler(clazz: Class<out BotAction<*>?>): BotAction<*>? {
        return actions.values.stream().filter { o: BotAction<*>? -> clazz.isInstance(o) }.findFirst().orElse(null)
    }

    private fun initAction(actionClass: Class<out NT_Action?>?, botAction: BotAction<*>) {
        botAction.init(game, bot, strategy, this)
        actions[actionClass] = botAction
    }

    fun react(action: NT_Action): NT_Reaction? {
        val botAction = actions[action.javaClass]
                ?: throw RuntimeException("Bot unable to react to $action")
        return botAction.perform(action)
    }

    fun createBestReaction(turn: NT_PlayerTurnActions): Any? {
        var bestAction: BotOptionalAction<*, *>? = null
        var bestOption: BotOption = endTurnOption
        var ntAction: NT_Action? = null
        for (action in turn.actions) {
            val botAction = actions[action.javaClass] as BotOptionalAction<*, *>?
            val botOption = botAction!!.score(action)
            if (botOption != null) {
                Log.trace(bot.toString() + " scored [" + botOption.score + "] for option " + botOption)
                if (botOption.score > bestOption.score) {
                    bestOption = botOption
                    bestAction = botAction
                    ntAction = action
                }
            }
        }
        Log.trace(bot.toString() + " picked best option: " + bestOption)
        if (bestOption === endTurnOption) {
            return NT_EndTurn()
        }
        bestAction!!.setSelectedOption(bestOption)
        return bestAction.perform(ntAction)
    }

    private inner class EndTurnOption : BotOption(0f) {
        override fun toString(): String {
            return "endturn"
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotActionHandler::class.java)
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.bot.impl"
    }
}
