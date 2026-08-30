package com.broll.gainea.server.init

import com.broll.gainea.net.proto.NT_PlayerSettings
import com.broll.gainea.core.fractions.FractionType
import com.broll.gainea.server.core.player.Player

class PlayerData(var fraction: FractionType) {
    lateinit var gamePlayer: Player
    var ready = false
    fun joinedGame(gamePlayer: Player) {
        this.gamePlayer = gamePlayer
        gamePlayer.fraction
    }

    fun nt(): NT_PlayerSettings = NT_PlayerSettings.newBuilder()
        .setFraction(fraction.ordinal)
        .setReady(ready)
        .build()
}
