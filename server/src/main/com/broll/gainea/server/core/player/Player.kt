package com.broll.gainea.server.core.player

import com.broll.gainea.net.NT_Player
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.LobbyPlayer
import java.util.stream.Collectors

class Player(game: GameContainer, val fraction: Fraction, val serverPlayer: LobbyPlayer<PlayerData>) {
    val units = mutableListOf<Unit>()
    val goalHandler: GoalHandler
    val cardHandler: CardHandler
    var skipRounds = 0
        private set
    private var color = 0
    private var surrendered = false

    init {
        goalHandler = GoalHandler(game, this)
        cardHandler = CardHandler(game, this)
        serverPlayer.data.joinedGame(this)
    }

    val active: Boolean
        get() = !surrendered && !serverPlayer.hasLeftLobby()

    fun surrender() {
        surrendered = true
    }

    fun hasSurrendered(): Boolean {
        return surrendered
    }

    fun setColor(color: Int) {
        this.color = color
    }

    fun skipRounds(rounds: Int) {
        skipRounds += rounds
    }

    fun consumeSkippedRound() {
        skipRounds--
        if (skipRounds < 0) {
            skipRounds = 0
        }
    }

    val controlledLocations: List<Location>
        get() =  units.mapNotNull { it.location }

    fun nt(): NT_Player {
        val player = NT_Player()
        player.cards = cardHandler.cardCount.toByte()
        player.fraction = fraction.type.ordinal.toByte()
        player.id = serverPlayer.id.toShort()
        player.color = color.toByte()
        player.stars = goalHandler.stars.toShort()
        player.name = serverPlayer.name
        player.points = goalHandler.score.toByte()
        player.units = units.map { it.nt() }.toTypedArray()
        return player
    }

    override fun toString(): String {
        return serverPlayer.toString()
    }
}
