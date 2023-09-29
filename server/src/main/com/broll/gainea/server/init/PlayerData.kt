package com.broll.gainea.server.init

import com.broll.gainea.net.NT_PlayerSettings
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.impl.ILobbyData

class PlayerData(var fraction: FractionType) : ILobbyData {
    lateinit var gamePlayer: Player
    var isReady = false
    fun joinedGame(gamePlayer: Player) {
        this.gamePlayer = gamePlayer
        gamePlayer.fraction
    }

    override fun nt(): NT_PlayerSettings {
        val playerSettings = NT_PlayerSettings()
        playerSettings.fraction = fraction.ordinal
        playerSettings.ready = isReady
        return playerSettings
    }
}
