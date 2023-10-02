package com.broll.gainea.server.core.bot

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_EndTurn
import com.broll.gainea.net.NT_PlayerTurnActions
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.player.Player
import org.slf4j.LoggerFactory
import java.util.function.Consumer


class BotActionHandler(private val game: Game, private val bot: Player, private val strategy: BotStrategy) {
    private val actions: MutableMap<Class<out NT_Action>, BotAction<NT_Action>> = HashMap()
    private val endTurnOption = EndTurnOption()

    init {
        PackageLoader(BotAction::class.java, PACKAGE_PATH).instantiateAll().forEach(Consumer { a: BotAction<*> -> initAction(a.actionClass, a as BotAction<NT_Action>) })
    }

    fun <T : BotAction<*>> getActionHandler(clazz: Class<T>): T = actions.values.first { clazz.isInstance(it) } as T

    private fun initAction(actionClass: Class<out NT_Action>, botAction: BotAction<NT_Action>) {
        botAction.init(game, bot, strategy, this)
        actions[actionClass] = botAction
    }

    fun react(action: NT_Action): NT_Reaction {
        val botAction = actions[action.javaClass]
                ?: throw RuntimeException("Bot unable to react to $action")
        return botAction.perform(action)
    }

    fun createBestReaction(turn: NT_PlayerTurnActions): Any {
        var bestAction: BotOptionalAction<NT_Action, BotOptionalAction.BotOption>? = null
        var bestOption: BotOptionalAction.BotOption = endTurnOption
        var ntAction: NT_Action? = null
        for (action in turn.actions) {
            val botAction = actions[action.javaClass] as BotOptionalAction<NT_Action, BotOptionalAction.BotOption>
            val botOption = botAction.score(action)
            if (botOption != null) {
                Log.trace(bot.toString() + " scored [" + botOption.score + "] for option " + botOption)
                if (botOption.score > bestOption.score) {
                    bestOption = botOption
                    bestAction = botAction
                    ntAction = action
                }
            }
        }
        Log.trace("{} picked best option: {}", bot, bestOption)
        if (bestOption === endTurnOption) {
            return NT_EndTurn()
        }
        bestAction!!.setSelectedOption(bestOption)
        return bestAction.perform(ntAction!!)
    }

    private inner class EndTurnOption : BotOptionalAction.BotOption(0f) {
        override fun toString(): String {
            return "endturn"
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotActionHandler::class.java)
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.bot.impl"
    }
}
