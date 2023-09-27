package com.broll.gainea.server.core.actions

import com.broll.gainea.net.NT_EndTurn
import com.broll.gainea.net.NT_GameOver
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.init.PlayerData
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.function.Consumer

class ReactionHandler(private val game: GameContainer, val actionHandlers: ActionHandlers) {
    private val requiredActions = Collections.synchronizedMap(HashMap<RequiredActionContext<*>, RequiredAction>())
    private var gameStopped = false
    fun requireAction(target: Player?, context: RequiredActionContext<*>) {
        val action = RequiredAction()
        action.player = target
        action.context = context
        game.pushAction(context)
        requiredActions[context] = action
    }

    @Synchronized
    fun finishedProcessing() {
        Log.trace("Processing core finished")
        tryContinueTurn()
    }

    private fun tryContinueTurn() {
        if (GameUtils.noActivePlayersRemaining(game) || game.isGameOver) {
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
        GameUtils.sendUpdate(game, gameOver)
        game.statistic.sendStatistic()
        val lobby = game.lobby
        lobby!!.data.game = null
        lobby.unlock()
        lobby.realPlayers.forEach(Consumer { p: com.broll.networklib.server.impl.Player<PlayerData?> -> p.data.setReady(false) })
        lobby.sendLobbyUpdate()
    }

    private fun continueTurn() {
        val activePlayer = game.currentPlayer
        val turn = game.turnBuilder.build(activePlayer)
        Log.trace("Continue " + activePlayer + " turn [" + turn!!.actions.size + " optional actions]")
        if (turn.actions.size == 0) {
            //no more actions available, player can only end turn
            activePlayer.serverPlayer.sendTCP(NT_EndTurn())
        } else {
            activePlayer.serverPlayer.sendTCP(turn)
        }
    }

    fun playerReconnected(player: Player?) {
        Log.info(player.toString() + " reconnected to game")
        //re send required actions for this player
        requiredActions.values.stream().filter { ra: RequiredAction -> ra.player === player }.findFirst().ifPresent { requiredAction: RequiredAction -> player.getServerPlayer().sendTCP(requiredAction.context!!.nt()) }
        if (game.isPlayersTurn(player) && !game.processingCore.isBusy && !game.battleHandler.isBattleActive) {
            //resend remaining optional actions
            tryContinueTurn()
        }
    }

    @Synchronized
    fun hasRequiredActionFor(player: Player?): Boolean {
        return requiredActions.values.stream().anyMatch { ra: RequiredAction -> ra.player === player }
    }

    fun handle(gamePlayer: Player?, actionContext: ActionContext<*>?, reaction: NT_Reaction) {
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
                    handleReaction(gamePlayer, ra.context.getActionContext(), reaction)
                    //consume required action
                    requiredActions.remove(actionContext)
                    game.consumeAction(ra.context.getAction().actionId)
                } else {
                    Log.warn("Ingore required reaction because its sent by the wrong player")
                }
            } else {
                Log.warn("Ingore optional action because of required action")
            }
        }
    }

    private fun handleReaction(gamePlayer: Player?, actionContext: ActionContext<*>?, reaction: NT_Reaction) {
        val action = actionContext.getAction()
        val customHandler = actionContext.getCustomHandler()
        val completionListener = actionContext.getCompletionListener()
        if (customHandler != null) {
            customHandler.handleReaction(actionContext, reaction)
        } else {
            val actionHandler: AbstractActionHandler<*, *>? = actionHandlers.getHandlerForAction(action!!.javaClass)
            if (actionHandler != null) {
                actionHandler.update(gamePlayer)
                actionHandler.handleReaction(actionContext, action, reaction)
            } else {
                Log.error("No actionHandler found for action $action")
            }
        }
        completionListener?.completed(actionContext)
        game.consumeAction(action!!.actionId)
    }

    private inner class RequiredAction {
        var context: RequiredActionContext<*>? = null
        var player: Player? = null
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ReactionHandler::class.java)
    }
}
