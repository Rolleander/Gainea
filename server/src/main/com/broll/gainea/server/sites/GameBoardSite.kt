package com.broll.gainea.server.sites

import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.net.NT_EndTurn
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.net.NT_Surrender
import com.broll.gainea.server.core.utils.displayMessage
import com.broll.gainea.server.core.utils.endPlayersTurn
import com.broll.gainea.server.core.utils.noActivePlayersRemaining
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.ConnectionRestriction
import com.broll.networklib.server.RestrictionType
import org.slf4j.LoggerFactory

class GameBoardSite : GameSite() {
    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun reaction(reaction: NT_Reaction) {
        if (!game.isGameOver) {
            val action = game.getAction(reaction.actionId)
            if (action == null) {
                Log.warn("Ignore reaction, no action found for id " + reaction.actionId)
                //invalid action, ignore client request
                return
            }
            val handler = game.reactionHandler
            val player = gamePlayer
            if (handler.hasRequiredActionFor(player)) {
                handler.handle(player, action, reaction)
            } else {
                //only handle optional actions when its the players turn
                if (playersTurn()) {
                    handler.handle(player, action, reaction)
                } else {
                    Log.warn("Ignore optional action because its not players turn")
                }
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun reaction(battle_reaction: NT_Battle_Reaction) {
        val battle = game.battleHandler
        if (battle.isBattleActive) {
            battle.playerReaction(gamePlayer, battle_reaction)
        } else {
            Log.warn("Battle reaction ignored because battle is not active")
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun endTurn(nt: NT_EndTurn) {
        //only react to if its players turn and no action is running right now
        if (lobby.data.gameRoundsStarted &&
            !game.isGameOver && playersTurn() &&
            !game.processingCore.isBusy
        ) {
            //dont allow next turn if there are required actions for the player remaining
            if (!game.reactionHandler.hasRequiredActionFor(gamePlayer)) {
                nextTurn()
            } else {
                Log.warn("End turn ignored because there are required actions remaining")
            }
        } else {
            Log.warn("End turn ignored because game is over, its not the players turn or processing core is busy")
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun surrender(nt: NT_Surrender) {
        if (gamePlayer.hasSurrendered()) {
            return
        }
        Log.info(player.name + " surrendered!")
        gamePlayer.surrender()
        if (game.noActivePlayersRemaining()) {
            game.end()
            game.processingCore.shutdown()
            game.reactionHandler.finishedProcessing()
        } else {
            game.displayMessage(player.name + " hat aufgegeben!", sound = "smash.ogg")
            game.endPlayersTurn(gamePlayer)
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GameBoardSite::class.java)
    }
}


