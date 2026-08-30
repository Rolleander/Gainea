package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.endTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.playerData
import com.broll.mpnll.server.site.NetworkSite

abstract class GameSite : NetworkSite() {
    protected val game: Game
        get() = lobby.gameData.game!!
    protected val gamePlayer: Player
        get() = user.playerData.gamePlayer
    protected val playersCount: Int
        get() = game.allPlayers.size

    protected fun playersTurn() = gamePlayer === game.currentPlayer && gamePlayer.skipRounds <= 0


    protected fun nextTurn() {
        game.endTurn()
    }
}
