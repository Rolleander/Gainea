package com.broll.gainea.server.core

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_PlayerTurnStart
import com.broll.gainea.net.NT_PlayerWait
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.actions.RequiredActionContext
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.GameUtils
import com.broll.gainea.server.core.utils.MessageUtils
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.ServerLobby
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class ReactionResultHandler(private val game: GameContainer, private val lobby: ServerLobby<LobbyData, PlayerData>) : ReactionActions {
    override fun sendGameUpdate(update: Any) {
        lobby.sendToAllTCP(update)
    }

    override fun endTurn() {
        game.processingCore.execute {
            val player = game.nextTurn()
            val newRound = game.currentTurn == 0
            if (player == null) {
                //no more turns
                game.end()
                return@execute
            }
            sendBoardUpdate()
            if (newRound) {
                MessageUtils.gameLog(game, "-- Runde " + game.rounds + " --")
                game.updateReceiver.roundStarted()
            }
            Log.info("Start next turn: " + player + " [Round " + game.rounds + " Turn " + (game.currentTurn + 1) + " / " + game.allPlayers.size + "]")
            game.updateReceiver.turnStarted(player)
            if (!checkPlayerSkipped(player)) {
                doPlayerTurn(player)
            }
        }
    }

    private fun checkPlayerSkipped(player: Player): Boolean {
        val skip = player.skipRounds > 0 || !player.active
        if (skip) {
            Log.debug("Player turn skipped!")
            player.consumeSkippedRound()
            var delay = 0
            if (player.active) {
                //send aussetzen info to all players
                MessageUtils.displayMessage(game, player.serverPlayer.name + " muss aussetzen!")
                delay = 3000
            }
            //auto start next round after delay
            game.processingCore.execute({ endTurn() }, delay)
        }
        return skip
    }

    private fun doPlayerTurn(player: Player) {
        //send turnstart to player and wait to other players
        val wait = NT_PlayerWait()
        wait.playersTurn = player.serverPlayer.id
        GameUtils.sendUpdate(game, player, NT_PlayerTurnStart(), wait)
        ProcessingUtils.pause(1000)
        val fraction = player.fraction
        val actionsHandler = game.reactionHandler.actionHandlers
        //do fraction turn prepare
        fraction!!.prepareTurn(actionsHandler)
        //send turn actions to player
        player.units.forEach(Consumer { obj: Unit? -> obj!!.turnStart() })
        val turn = game.turnBuilder.build(player)
        Log.trace("Send optional turn actions (" + turn!!.actions.size + ") to player " + player)
        player.serverPlayer.sendTCP(turn)
        //do fraction turn started
        fraction.turnStarted(actionsHandler)
    }

    override fun sendBoardUpdate() {
        sendGameUpdate(game.nt())
    }

    override fun requireAction(player: Player, action: RequiredActionContext<NT_Action>): ActionContext<NT_Action> {
        Log.trace("Require action for " + player + " : " + action.action)
        game.reactionHandler.requireAction(player, action)
        player.serverPlayer.sendTCP(action.nt())
        if (action.messageForOtherPlayer != null) {
            GameUtils.sendUpdateExceptFor(game, action.messageForOtherPlayer, player)
        }
        return action
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ReactionResultHandler::class.java)
    }
}
