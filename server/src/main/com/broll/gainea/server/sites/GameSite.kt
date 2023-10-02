package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.LobbyServerSite

abstract class GameSite : LobbyServerSite<LobbyData, PlayerData>() {
    protected val game: Game
        get() = lobby.data!!.game!!
    protected val gamePlayer: Player
        get() = player.data!!.gamePlayer
    protected val playersCount: Int
        get() = game.allPlayers.size

    protected fun playersTurn() = gamePlayer === game.currentPlayer && gamePlayer.skipRounds <= 0


    protected fun nextTurn() {
        game.reactionHandler.actionHandlers.reactionActions.endTurn()
    }
}
