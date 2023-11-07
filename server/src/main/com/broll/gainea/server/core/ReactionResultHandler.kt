package com.broll.gainea.server.core

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_PlayerTurnStart
import com.broll.gainea.net.NT_PlayerWait
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.actions.RequiredActionContext
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.displayMessage
import com.broll.gainea.server.core.utils.gameLog
import com.broll.gainea.server.core.utils.sendUpdate
import com.broll.gainea.server.core.utils.sendUpdateExceptFor
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.ServerLobby
import org.slf4j.LoggerFactory

class ReactionResultHandler(
    private val game: Game,
    private val lobby: ServerLobby<LobbyData, PlayerData>
) : ReactionActions {
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
                game.gameLog("-- Runde " + game.rounds + " --")
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
                game.displayMessage(
                    player.serverPlayer.name + " muss aussetzen!",
                    sound = "smash.ogg"
                )
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
        game.sendUpdate(player, NT_PlayerTurnStart(), wait)
        ProcessingUtils.pause(1000)
        player.fraction.prepareTurn()
        //send turn actions to player
        val turn = game.turnBuilder.build(player)
        Log.trace("Send optional turn actions (" + turn.actions.size + ") to player " + player)
        player.serverPlayer.sendTCP(turn)
    }

    override fun sendBoardUpdate() {
        sendGameUpdate(game.nt())
    }

    override fun requireAction(
        player: Player,
        action: RequiredActionContext<out NT_Action>
    ): ActionContext<out NT_Action> {
        Log.trace("Require action for " + player + " : " + action.action)
        game.reactionHandler.requireAction(player, action)
        player.serverPlayer.sendTCP(action.nt())
        if (action.messageForOtherPlayer != null) {
            game.sendUpdateExceptFor(action.messageForOtherPlayer, player)
        }
        return action
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ReactionResultHandler::class.java)
    }
}
