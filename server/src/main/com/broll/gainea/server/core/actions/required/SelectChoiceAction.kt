package com.broll.gainea.server.core.actions.required

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Action_SelectChoice
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.RequiredActionContext
import com.broll.gainea.server.core.actions.required.SelectChoiceAction.Context
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getOtherPlayers
import org.slf4j.LoggerFactory

class SelectChoiceAction :
    AbstractActionHandler<NT_Action_SelectChoice, Context>() {
    inner class Context(
        action: NT_Action_SelectChoice,
        var selectedOption: Int = 0
    ) : ActionContext<NT_Action_SelectChoice>(action)

    fun selection(message: String, choices: List<String>): Int {
        return selection(game.currentPlayer, message, choices)
    }

    fun selection(player: Player, message: String, choices: List<String>): Int {
        Log.debug("Select choice action")
        val context = build(choices)
        actionHandlers.reactionActions.requireAction(
            player,
            RequiredActionContext(context, message)
        )
        Log.trace("Wait for select choice reaction")
        processingBlock.waitFor(player)
        if (processingBlock.playerLeft) {
            context.selectedOption = RandomUtils.randomIndex(choices)
        }
        return context.selectedOption
    }

    fun selectObject(message: String, choices: List<Any>): Int {
        return selectObject(game.currentPlayer, message, choices)
    }

    fun selectObject(player: Player, message: String, choices: List<Any>): Int {
        assureNotEmpty(choices)
        Log.debug("Select object action")
        val action = NT_Action_SelectChoice()
        action.objectChoices = choices.toTypedArray<Any>()
        val context = Context(action)
        actionHandlers.reactionActions.requireAction(
            player,
            RequiredActionContext(context, message)
        )
        Log.trace("Wait for select choice reaction")
        processingBlock.waitFor(player)
        if (processingBlock.playerLeft) {
            context.selectedOption = RandomUtils.randomIndex(choices)
        }
        return context.selectedOption
    }

    fun selectLocation(message: String, choices: List<Location>): Location {
        return selectLocation(game.currentPlayer, message, choices)
    }

    fun selectLocation(player: Player, message: String, choices: List<Location>): Location {
        assureNotEmpty(choices)
        Log.debug("Select location action")
        val action = NT_Action_SelectChoice()
        val distinctLocations = choices.distinct()
        action.objectChoices = distinctLocations.map { it.number }.toTypedArray()
        val context = Context(action)
        actionHandlers.reactionActions.requireAction(
            player,
            RequiredActionContext(context, message)
        )
        Log.trace("Wait for select choice reaction")
        processingBlock.waitFor(player)
        if (processingBlock.playerLeft) {
            context.selectedOption = RandomUtils.randomIndex(choices)
        }
        return distinctLocations[context.selectedOption]
    }

    fun selectOtherPlayer(player: Player, message: String): Player {
        return selectPlayer(player, game.getOtherPlayers(player), message)
    }

    fun selectPlayer(player: Player, options: List<Player>, message: String): Player {
        Log.debug("Select player action")
        val context = build(options.map { it.serverPlayer.name })
        actionHandlers.reactionActions.requireAction(
            player,
            RequiredActionContext(context, message)
        )
        Log.trace("Wait for select choice reaction")
        processingBlock.waitFor(player)
        if (processingBlock.playerLeft) {
            context.selectedOption = RandomUtils.randomIndex(options)
        }
        return options[context.selectedOption]
    }

    private fun assureNotEmpty(choices: List<*>) {
        if (choices.isEmpty()) {
            throw RuntimeException("Invalid select choice context: list of choices is empty")
        }
    }

    private fun build(choices: List<String>): Context {
        assureNotEmpty(choices)
        val action = NT_Action_SelectChoice()
        action.choices = choices.toTypedArray<String>()
        return Context(action)
    }

    override fun handleReaction(
        context: Context,
        action: NT_Action_SelectChoice,
        reaction: NT_Reaction
    ) {
        Log.trace("Handle select choice reaction")
        context.selectedOption = reaction.option
        processingBlock.resume()
    }
    
    companion object {
        private val Log = LoggerFactory.getLogger(SelectChoiceAction::class.java)
    }


}
