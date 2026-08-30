package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.map.*

import com.broll.gainea.net.proto.NT_Player
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.shop.Shop
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.playerData
import com.broll.mpnll.server.user.User

open class Player(game: Game, val fraction: Fraction, val serverPlayer: User) {
    val units = mutableListOf<Unit>()
    val goalHandler: GoalHandler
    val cardHandler: CardHandler
    val shop: Shop

    var skipRounds = 0
        private set
    private var color = 0
    private var surrendered = false

    init {
        fraction.init(game, this)
        goalHandler = GoalHandler(game, this)
        cardHandler = CardHandler(game, this)
        shop = Shop(game, this)
    }

    val active: Boolean
        get() = !surrendered && serverPlayer.lobby?.isActiveMember(serverPlayer) == true

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

    val controlledLocations: Set<Location>
        get() = units.mapNotNull { it.location }.toSet()

    fun nt(): NT_Player {
        return NT_Player.newBuilder().setCards(cardHandler.cardCount)
            .setFraction(fraction.type.ordinal).setId(serverPlayer.id).setColor(color)
            .setStars(goalHandler.stars).setName(serverPlayer.name).setPoints(goalHandler.score)
            .addAllUnits(units.map { it.nt() }).build()
    }

    override fun toString(): String {
        return serverPlayer.toString()
    }
}
