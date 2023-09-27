package com.broll.gainea.server.sites

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.LobbyServerSite

abstract class GameSite : LobbyServerSite<LobbyData?, PlayerData?>() {
    protected val game: GameContainer?
        protected get() = lobby.data.getGame()
    protected val gamePlayer: Player?
        protected get() = player.data.getGamePlayer()
    protected val playersCount: Int
        protected get() = game.getAllPlayers().size

    protected fun playersTurn(): Boolean {
        return gamePlayer === game.getCurrentPlayer() && gamePlayer.getSkipRounds() <= 0
    }

    protected fun nextTurn() {
        game.getReactionHandler().actionHandlers.reactionActions.endTurn()
    }
}
