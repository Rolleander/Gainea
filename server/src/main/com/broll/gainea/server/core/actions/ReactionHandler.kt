package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_EndTurn
import com.broll.gainea.net.NT_GameOver
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.noActivePlayersRemaining
import com.broll.gainea.server.core.utils.sendUpdate
import org.slf4j.LoggerFactory
import java.util.Collections

class ReactionHandler(private val game: Game, val actionHandlers: ActionHandlers) {
    private val requiredActions = Collections.synchronizedMap(HashMap<RequiredActionContext<out NT_Action>, RequiredAction>())
    private var gameStopped = false
    fun requireAction(target: Player, context: RequiredActionContext<out NT_Action>) {
        val action = RequiredAction(context, target)
        game.pushAction(context)
        requiredActions[context] = action
    }

    @Synchronized
    fun finishedProcessing() {
        Log.trace("Processing core finished")
        tryContinueTurn()
    }

    private fun tryContinueTurn() {
        if (game.noActivePlayersRemaining() || game.isGameOver) {
            stopGame()
            return
        }
        if (requiredActions.isEmpty()) {
            if (game.currentPlayer.active) {
                continueTurn()
            } else {
                //end inactive players turn
                actionHandlers.reactionActions.endTurn()
            }
        }
    }

    private fun stopGame() {
        if (gameStopped) {
            return
        }
        gameStopped = true
        game.processingCore.shutdown()
        Log.trace("Process gameend")
        val gameOver = NT_GameOver()
        game.fillUpdate(gameOver)
        game.sendUpdate(gameOver)
        game.statistic.sendStatistic()
        val lobby = game.lobby
        lobby.data.game = null
        lobby.unlock()
        lobby.realPlayers.forEach { it.data.ready = false }
        lobby.sendLobbyUpdate()
    }

    private fun continueTurn() {
        val activePlayer = game.currentPlayer
        val turn = game.turnBuilder.build(activePlayer)
        Log.trace("Continue " + activePlayer + " turn [" + turn.actions.size + " optional actions]")
        if (turn.actions.isEmpty()) {
            //no more actions available, player can only end turn
            activePlayer.serverPlayer.sendTCP(NT_EndTurn())
        } else {
            activePlayer.serverPlayer.sendTCP(turn)
        }
    }

    fun playerReconnected(player: Player) {
        Log.info(player.toString() + " reconnected to game")
        //re send required actions for this player
        requiredActions.values.firstOrNull { it.player === player }?.let {
            player.serverPlayer.sendTCP(it.context.nt())
        }
        if (game.isPlayersTurn(player) && !game.processingCore.isBusy && !game.battleHandler.isBattleActive) {
            //resend remaining optional actions
            tryContinueTurn()
        }
    }

    @Synchronized
    fun hasRequiredActionFor(player: Player) = requiredActions.values.any { it.player == player }

    fun handle(gamePlayer: Player, actionContext: ActionContext<out NT_Action>, reaction: NT_Reaction) {
        if (game.battleHandler.isBattleActive) {
            //ignore reactions while fight is going on
            Log.warn("Ignore player reaction because of fight!")
            return
        }
        if (requiredActions.isEmpty()) {
            if (game.processingCore.isBusy) {
                //player action is not handled, game still processing actions
                Log.warn("Ignore player optional action because processing core is busy!")
                return
            }
            //handle optional action
            handleReaction(gamePlayer, actionContext, reaction)
        } else {
            //handle required actions only
            val ra = requiredActions[actionContext]
            if (ra != null) {
                if (gamePlayer === ra.player) {
                    handleReaction(gamePlayer, ra.context.actionContext, reaction)
                    //consume required action
                    requiredActions.remove(actionContext)
                    game.consumeAction(ra.context.action.actionId)
                } else {
                    Log.warn("Ingore required reaction because its sent by the wrong player")
                }
            } else {
                Log.warn("Ingore optional action because of required action")
            }
        }
    }

    private fun handleReaction(gamePlayer: Player, actionContext: ActionContext<out NT_Action>, reaction: NT_Reaction) {
        val action = actionContext.action
        val customHandler = actionContext.customHandler
        if (customHandler != null) {
            customHandler.handleReaction(actionContext, reaction)
        } else {
            val actionHandler = actionHandlers.getHandlerForAction(action.javaClass)
            if (actionHandler != null) {
                actionHandler.update(gamePlayer)
                actionHandler.handleReaction(actionContext, action, reaction)
            } else {
                Log.error("No actionHandler found for action $action")
            }
        }
        actionContext.completionListener?.completed(actionContext)
        game.consumeAction(action.actionId)
    }

    private inner class RequiredAction(
            val context: RequiredActionContext<out NT_Action>,
            val player: Player
    )

    companion object {
        private val Log = LoggerFactory.getLogger(ReactionHandler::class.java)
    }
}
